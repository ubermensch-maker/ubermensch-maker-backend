package com.example.todo.message;

import com.example.todo.conversation.Conversation;
import com.example.todo.conversation.ConversationRepository;
import com.example.todo.conversation.ConversationService;
import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.dto.MessageCreateDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.MessageListDto;
import com.example.todo.message.dto.TextContentDto;
import com.example.todo.message.dto.ToolContentDto;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.message.enums.Model;
import com.example.todo.openai.OpenAIService;
import com.example.todo.toolcall.ToolCall;
import com.example.todo.toolcall.ToolCallRepository;
import com.example.todo.toolcall.enums.ToolCallSource;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionMessageToolCall;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MessageService {
  private static final int CONTEXT_LIMIT = 10;
  private static final int TITLE_MAX_LENGTH = 50;
  private static final int TITLE_PROMPT_MAX_LENGTH = 200;

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ConversationRepository conversationRepository;
  private final OpenAIService openAIService;
  private final ConversationService conversationService;
  private final ToolCallRepository toolCallRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public MessageDto create(Long userId, MessageCreateDto request) {
    // 1. validate user
    User user = validateAndGetUser(userId);

    // 2. prepare conversation (create new or fetch existing)
    boolean isNewConversation = request.getConversationId() == null;
    Conversation conversation = prepareConversation(user, request.getConversationId());

    // 3. create and save user message
    Message inputMessage = createAndSaveUserMessage(user, conversation, request);

    // 4. call openai api and process response
    ChatCompletion chatCompletion =
        callOpenAIWithContext(conversation, request.getModel(), user, inputMessage);

    // 5. create ai response message (including tool calls)
    Message outputMessage =
        createAssistantMessage(
            user, conversation, inputMessage, chatCompletion, request.getModel());

    // 6. update conversation title (if first message or new conversation)
    if (shouldUpdateTitle(inputMessage.getIndex(), isNewConversation)) {
      updateConversationTitle(conversation, inputMessage, outputMessage, user);
    }

    // 7. build response dto
    return buildResponseDto(outputMessage, conversation, isNewConversation);
  }

  // ===== validation and query methods =====

  private User validateAndGetUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  private Conversation prepareConversation(User user, UUID conversationId) {
    if (conversationId == null) {
      return conversationRepository.save(Conversation.create(user, "New Chat"));
    }

    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    // check permissions
    if (!user.getId().equals(conversation.getUser().getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this conversation");
    }

    return conversation;
  }

  private Message findParentMessage(
      UUID parentMessageId, UUID conversationId, Integer messageIndex) {
    // if explicit parent message id is provided
    if (parentMessageId != null) {
      return messageRepository
          .findById(parentMessageId)
          .orElseThrow(
              () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent message not found"));
    }

    // if no parent message id and not first message, set previous message as parent
    if (messageIndex > 0) {
      return messageRepository
          .findTopByConversationIdAndIndexLessThanOrderByIndexDesc(conversationId, messageIndex)
          .orElse(null);
    }

    return null;
  }

  // ===== message creation methods =====

  private Message createAndSaveUserMessage(
      User user, Conversation conversation, MessageCreateDto request) {
    Integer messageIndex = getNextMessageIndex(conversation.getId());
    Message parentMessage =
        findParentMessage(request.getParentMessageId(), conversation.getId(), messageIndex);

    Message inputMessage =
        Message.create(
            user,
            conversation,
            parentMessage,
            messageIndex,
            request.getModel(),
            request.getRole(),
            request.getContent());

    return messageRepository.save(inputMessage);
  }

  private Message createAssistantMessage(
      User user,
      Conversation conversation,
      Message parentMessage,
      ChatCompletion chatCompletion,
      Model model) {
    // 1. process tool calls
    ToolCallResult toolCallResult = processToolCalls(user, chatCompletion);

    // 2. build response contents
    List<ContentDto> outputContents =
        buildOutputContents(chatCompletion, toolCallResult.toolContents);

    // 3. create and save assistant message
    Integer messageIndex = getNextMessageIndex(conversation.getId());
    Message outputMessage =
        Message.create(
            null,
            conversation,
            parentMessage,
            messageIndex,
            model,
            MessageRole.ASSISTANT,
            outputContents);
    messageRepository.save(outputMessage);

    // 4. update and save tool call entities
    saveToolCallsWithMessage(
        user, outputMessage, toolCallResult.toolCalls, outputContents, toolCallResult.toolContents);

    return outputMessage;
  }

  // ===== tool call processing methods =====

  private static class ToolCallResult {
    final List<ToolCall> toolCalls;
    final List<ToolContentDto> toolContents;

    ToolCallResult(List<ToolCall> toolCalls, List<ToolContentDto> toolContents) {
      this.toolCalls = toolCalls;
      this.toolContents = toolContents;
    }
  }

  private ToolCallResult processToolCalls(User user, ChatCompletion chatCompletion) {
    List<ToolCall> toolCalls = new ArrayList<>();
    List<ToolContentDto> toolContents = new ArrayList<>();

    for (ChatCompletion.Choice choice : chatCompletion.choices()) {
      if (choice.message().toolCalls().isPresent()) {
        for (ChatCompletionMessageToolCall toolCall : choice.message().toolCalls().get()) {
          processIndividualToolCall(user, toolCall, toolCalls, toolContents);
        }
      }
    }

    return new ToolCallResult(toolCalls, toolContents);
  }

  private void processIndividualToolCall(
      User user,
      ChatCompletionMessageToolCall toolCall,
      List<ToolCall> toolCalls,
      List<ToolContentDto> toolContents) {
    Map<String, Object> arguments = parseToolArguments(toolCall);

    // create tool call entity (message reference will be set later)
    ToolCall toolCallEntity =
        ToolCall.create(
            user,
            null, // will be set later
            toolCall.function().name(),
            arguments,
            ToolCallSource.OPENAI,
            toolCall.id(),
            null);
    toolCalls.add(toolCallEntity);

    // create tool content dto
    ToolContentDto toolContent =
        new ToolContentDto(
            toolCall.function().name(),
            arguments,
            null, // will be set after save
            toolCallEntity.getStatus(),
            toolCall.id());
    toolContents.add(toolContent);
  }

  private Map<String, Object> parseToolArguments(ChatCompletionMessageToolCall toolCall) {
    try {
      String argumentsJson = toolCall.function().arguments().toString();
      return objectMapper.readValue(argumentsJson, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to parse tool call arguments: " + e.getMessage(),
          e);
    }
  }

  private void saveToolCallsWithMessage(
      User user,
      Message message,
      List<ToolCall> toolCalls,
      List<ContentDto> outputContents,
      List<ToolContentDto> toolContents) {
    for (int i = 0; i < toolCalls.size(); i++) {
      ToolCall toolCall = toolCalls.get(i);

      // recreate tool call with message reference
      toolCall =
          ToolCall.create(
              user,
              message,
              toolCall.getToolName(),
              toolCall.getArguments(),
              toolCall.getSource(),
              toolCall.getSourceCallId(),
              toolCall.getSourceMetadata());
      toolCallRepository.save(toolCall);

      // update tool content with saved id
      updateToolContentWithId(toolContents.get(i), toolCall, outputContents);
    }
  }

  private void updateToolContentWithId(
      ToolContentDto originalContent, ToolCall savedToolCall, List<ContentDto> outputContents) {
    ToolContentDto updatedContent =
        new ToolContentDto(
            originalContent.getName(),
            originalContent.getArgs(),
            savedToolCall.getId(),
            savedToolCall.getStatus(),
            originalContent.getSourceCallId());

    int index = outputContents.indexOf(originalContent);
    if (index >= 0) {
      outputContents.set(index, updatedContent);
    }
  }

  private List<ContentDto> buildOutputContents(
      ChatCompletion chatCompletion, List<ToolContentDto> toolContents) {
    List<ContentDto> contents = new ArrayList<>();

    // add text response
    String textContent = chatCompletion.choices().get(0).message().content().orElse("");
    if (!textContent.isEmpty()) {
      contents.add(new TextContentDto(textContent));
    }

    // add tool call contents
    contents.addAll(toolContents);

    return contents;
  }

  // ===== openai api call methods =====

  private ChatCompletion callOpenAIWithContext(
      Conversation conversation, Model model, User user, Message currentMessage) {
    List<MessageDto> contextMessages = getConversationContext(conversation.getId());
    return openAIService.chatCompletionWithTools(contextMessages, model, user, currentMessage);
  }

  private List<MessageDto> getConversationContext(UUID conversationId) {
    Pageable pageable = PageRequest.of(0, CONTEXT_LIMIT);
    List<Message> recentMessages =
        messageRepository.findByConversationIdOrderByIndexDesc(conversationId, pageable);

    return recentMessages.stream()
        .sorted((m1, m2) -> m1.getIndex().compareTo(m2.getIndex()))
        .map(MessageDto::from)
        .toList();
  }

  // ===== conversation title methods =====

  private boolean shouldUpdateTitle(Integer messageIndex, boolean isNewConversation) {
    return messageIndex == 0 || isNewConversation;
  }

  private void updateConversationTitle(
      Conversation conversation, Message inputMessage, Message outputMessage, User user) {
    MessageDto inputDto = MessageDto.from(inputMessage);
    String outputText = extractTextContent(outputMessage);
    String title = generateConversationTitle(inputDto, outputText, user, outputMessage);

    conversationService.updateTitle(conversation.getId(), title);
    conversation.update(title);
  }

  private String extractTextContent(Message message) {
    return message.getContent().stream()
        .filter(content -> content instanceof TextContentDto)
        .map(content -> ((TextContentDto) content).getText())
        .findFirst()
        .orElse("");
  }

  private String generateConversationTitle(
      MessageDto inputMessage, String outputText, User user, Message message) {
    String inputText =
        inputMessage.getContent().stream()
            .filter(content -> content instanceof TextContentDto)
            .map(content -> ((TextContentDto) content).getText())
            .findFirst()
            .orElse("");

    String truncatedOutput =
        outputText.length() > TITLE_PROMPT_MAX_LENGTH
            ? outputText.substring(0, TITLE_PROMPT_MAX_LENGTH) + "..."
            : outputText;

    String prompt =
        String.format(
            "Generate a short title that summarizes the main topic (max %d characters):\n"
                + "User: %s\n"
                + "Assistant: %s\n\n"
                + "Title:",
            TITLE_MAX_LENGTH, inputText, truncatedOutput);

    MessageDto systemMessage = new MessageDto();
    systemMessage.setRole(MessageRole.SYSTEM);
    systemMessage.setContent(List.of(new TextContentDto(prompt)));

    String title =
        openAIService.chatCompletion(List.of(systemMessage), Model.GPT_4_1_NANO, user, message);

    // remove quotes and limit length
    title = title.replaceAll("^\"|\"$", "").trim();
    if (title.length() > TITLE_MAX_LENGTH) {
      title = title.substring(0, TITLE_MAX_LENGTH - 3) + "...";
    }

    return title;
  }

  // ===== response dto building methods =====

  private MessageDto buildResponseDto(
      Message outputMessage, Conversation conversation, boolean isNewConversation) {
    MessageDto responseDto = MessageDto.from(outputMessage);

    if (isNewConversation) {
      responseDto.setConversation(ConversationDto.from(conversation));
    }

    return responseDto;
  }

  // ===== existing public methods =====

  public MessageDto read(Long userId, UUID messageId) {
    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

    if (!userId.equals(message.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to read this message");
    }

    return MessageDto.from(message);
  }

  public MessageListDto list(Long userId, UUID conversationId) {
    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    if (!userId.equals(conversation.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to read this message list");
    }

    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    List<Message> messages = messageRepository.findAllByConversationId(conversationId, sort);

    return new MessageListDto(messages.size(), messages.stream().map(MessageDto::from).toList());
  }

  @Transactional
  public MessageDto processToolResult(Long userId, ToolCall toolCall, String toolResultMessage) {
    User user = validateAndGetUser(userId);
    Conversation conversation = toolCall.getMessage().getConversation();
    Message originalMessage = toolCall.getMessage();

    // create user message with tool execution result
    Message toolResultMsg =
        createToolResultMessage(user, conversation, originalMessage, toolResultMessage);

    // call openai api and process response
    ChatCompletion chatCompletion =
        callOpenAIWithContext(conversation, Model.GPT_4_1_NANO, user, toolResultMsg);

    // create assistant response message
    Message assistantResponse =
        createAssistantResponseForToolResult(user, conversation, toolResultMsg, chatCompletion);

    return MessageDto.from(assistantResponse);
  }

  private Message createToolResultMessage(
      User user, Conversation conversation, Message parentMessage, String toolResultMessage) {
    Integer messageIndex = getNextMessageIndex(conversation.getId());
    Message message =
        Message.create(
            user,
            conversation,
            parentMessage,
            messageIndex,
            Model.GPT_4_1_NANO,
            MessageRole.USER,
            List.of(new TextContentDto(toolResultMessage)));
    return messageRepository.save(message);
  }

  private Message createAssistantResponseForToolResult(
      User user, Conversation conversation, Message parentMessage, ChatCompletion chatCompletion) {
    // process tool calls
    ToolCallResult toolCallResult =
        processToolCallsForResponse(user, parentMessage, chatCompletion);

    // build response contents
    List<ContentDto> contents = buildResponseContents(chatCompletion, toolCallResult.toolContents);

    // create assistant message
    Integer messageIndex = getNextMessageIndex(conversation.getId());
    Message assistantResponse =
        Message.create(
            user,
            conversation,
            parentMessage,
            messageIndex,
            Model.GPT_4_1_NANO,
            MessageRole.ASSISTANT,
            contents);
    messageRepository.save(assistantResponse);

    return assistantResponse;
  }

  private ToolCallResult processToolCallsForResponse(
      User user, Message parentMessage, ChatCompletion chatCompletion) {
    List<ToolCall> toolCalls = new ArrayList<>();
    List<ToolContentDto> toolContents = new ArrayList<>();

    for (ChatCompletion.Choice choice : chatCompletion.choices()) {
      if (choice.message().toolCalls().isPresent()) {
        for (ChatCompletionMessageToolCall toolCallMsg : choice.message().toolCalls().get()) {
          Map<String, Object> arguments = parseToolArgumentsSafe(toolCallMsg);

          ToolCall newToolCall =
              ToolCall.create(
                  user,
                  parentMessage,
                  toolCallMsg.function().name(),
                  arguments,
                  ToolCallSource.OPENAI,
                  toolCallMsg.id(),
                  null);
          toolCallRepository.save(newToolCall);
          toolCalls.add(newToolCall);

          ToolContentDto toolContent =
              new ToolContentDto(
                  newToolCall.getToolName(),
                  arguments,
                  newToolCall.getId(),
                  newToolCall.getStatus(),
                  newToolCall.getSourceCallId());
          toolContents.add(toolContent);
        }
      }
    }

    return new ToolCallResult(toolCalls, toolContents);
  }

  private Map<String, Object> parseToolArgumentsSafe(ChatCompletionMessageToolCall toolCallMsg) {
    try {
      return objectMapper.readValue(
          toolCallMsg.function().arguments(), new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse tool arguments", e);
    }
  }

  private List<ContentDto> buildResponseContents(
      ChatCompletion chatCompletion, List<ToolContentDto> toolContents) {
    List<ContentDto> contents = new ArrayList<>();

    String textContent = chatCompletion.choices().get(0).message().content().orElse("");
    if (!textContent.isEmpty()) {
      contents.add(new TextContentDto(textContent));
    }

    contents.addAll(toolContents);
    return contents;
  }

  @Transactional
  public void delete(Long userId, UUID messageId) {
    User user = validateAndGetUser(userId);

    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

    if (!user.getId().equals(message.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to delete this message");
    }

    messageRepository.delete(message);
  }

  private Integer getNextMessageIndex(UUID conversationId) {
    return messageRepository
        .findTopByConversationIdOrderByIndexDesc(conversationId)
        .map(message -> message.getIndex() + 1)
        .orElse(0);
  }
}
