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

    // 2. prepare conversation
    boolean isNewConversation = request.getConversationId() == null;
    Conversation conversation = prepareConversation(user, request.getConversationId());

    // 3. create and save user message
    Message userMessage = createAndSaveUserMessage(user, conversation, request);

    // 4. call openai api
    ChatCompletion chatCompletion = callOpenAIWithContext(conversation, request.getModel());

    // 5. create assistant response
    Message assistantMessage =
        createAndSaveAssistantMessage(
            user, conversation, userMessage, chatCompletion, request.getModel());

    // 6. update conversation title if needed
    if (shouldUpdateTitle(userMessage.getIndex(), isNewConversation)) {
      updateConversationTitle(conversation, userMessage, assistantMessage, user);
    }

    // 7. build response
    return buildResponseDto(assistantMessage, conversation, isNewConversation);
  }

  @Transactional
  public MessageDto processToolResult(Long userId, ToolCall toolCall, String toolResultMessage) {
    User user = validateAndGetUser(userId);
    Conversation conversation = toolCall.getMessage().getConversation();
    Message originalMessage = toolCall.getMessage();

    // create system message with tool result
    Message toolResultMsg =
        createAndSaveMessage(
            user,
            conversation,
            originalMessage,
            Model.GPT_4_1_NANO,
            MessageRole.USER,
            List.of(new TextContentDto(toolResultMessage)));

    // call openai api
    ChatCompletion chatCompletion = callOpenAIWithContext(conversation, Model.GPT_4_1_NANO);

    // create assistant response
    Message assistantResponse =
        createAndSaveAssistantMessage(
            user, conversation, toolResultMsg, chatCompletion, Model.GPT_4_1_NANO);

    return MessageDto.from(assistantResponse);
  }

  public MessageDto get(Long userId, UUID messageId) {
    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

    validateMessageAccess(userId, message.getUser().getId(), "get this message");
    return MessageDto.from(message);
  }

  public MessageListDto list(Long userId, UUID conversationId) {
    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    validateMessageAccess(userId, conversation.getUser().getId(), "get this message list");

    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    List<Message> messages = messageRepository.findAllByConversationId(conversationId, sort);

    return new MessageListDto(messages.size(), messages.stream().map(MessageDto::from).toList());
  }

  @Transactional
  public void delete(Long userId, UUID messageId) {
    User user = validateAndGetUser(userId);

    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

    validateMessageAccess(user.getId(), message.getUser().getId(), "delete this message");
    messageRepository.delete(message);
  }

  private Message createAndSaveUserMessage(
      User user, Conversation conversation, MessageCreateDto request) {
    return createAndSaveMessage(
        user,
        conversation,
        findParentMessage(request.getParentMessageId(), conversation.getId()),
        request.getModel(),
        request.getRole(),
        request.getContent());
  }

  private Message createAndSaveMessage(
      User user,
      Conversation conversation,
      Message parentMessage,
      Model model,
      MessageRole role,
      List<ContentDto> content) {
    Integer messageIndex = getNextMessageIndex(conversation.getId());

    // auto-find parent if not provided
    if (parentMessage == null && messageIndex > 0) {
      parentMessage =
          messageRepository
              .findTopByConversationIdAndIndexLessThanOrderByIndexDesc(
                  conversation.getId(), messageIndex)
              .orElse(null);
    }

    Message message =
        Message.create(
            role == MessageRole.SYSTEM ? null : user,
            conversation,
            parentMessage,
            messageIndex,
            model,
            role,
            content);

    return messageRepository.save(message);
  }

  private Message createAndSaveAssistantMessage(
      User user,
      Conversation conversation,
      Message userMessage,
      ChatCompletion chatCompletion,
      Model model) {
    // extract tool calls without message reference
    ToolCallResult result = processToolCallsFromCompletion(user, chatCompletion, null);
    List<ContentDto> contents = buildMessageContents(chatCompletion, result.toolContents);

    // create assistant message
    Message assistantMessage =
        createAndSaveMessage(
            null, conversation, userMessage, model, MessageRole.ASSISTANT, contents);

    // save tool calls with the assistant message reference
    if (!result.toolCalls.isEmpty()) {
      saveToolCallsWithMessage(
          user, assistantMessage, result.toolCalls, contents, result.toolContents);
    }

    // save token usage
    saveTokenUsage(user, assistantMessage, model, chatCompletion);

    return assistantMessage;
  }

  private static class ToolCallResult {
    final List<ToolCall> toolCalls;
    final List<ToolContentDto> toolContents;

    ToolCallResult(List<ToolCall> toolCalls, List<ToolContentDto> toolContents) {
      this.toolCalls = toolCalls;
      this.toolContents = toolContents;
    }
  }

  private ToolCallResult processToolCallsFromCompletion(
      User user, ChatCompletion chatCompletion, Message parentMessage) {
    List<ToolCall> toolCalls = new ArrayList<>();
    List<ToolContentDto> toolContents = new ArrayList<>();

    for (ChatCompletion.Choice choice : chatCompletion.choices()) {
      if (choice.message().toolCalls().isPresent()) {
        for (ChatCompletionMessageToolCall toolCall : choice.message().toolCalls().get()) {
          processIndividualToolCall(user, toolCall, parentMessage, toolCalls, toolContents);
        }
      }
    }

    return new ToolCallResult(toolCalls, toolContents);
  }

  private void processIndividualToolCall(
      User user,
      ChatCompletionMessageToolCall toolCall,
      Message parentMessage,
      List<ToolCall> toolCalls,
      List<ToolContentDto> toolContents) {
    Map<String, Object> arguments = parseToolArguments(toolCall);

    ToolCall toolCallEntity =
        ToolCall.create(
            user,
            parentMessage,
            toolCall.function().name(),
            arguments,
            ToolCallSource.OPENAI,
            toolCall.id(),
            null);

    if (parentMessage != null) {
      toolCallRepository.save(toolCallEntity);
    }

    toolCalls.add(toolCallEntity);

    ToolContentDto toolContent =
        new ToolContentDto(
            toolCall.function().name(),
            arguments,
            parentMessage != null ? toolCallEntity.getId() : null,
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
          HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse tool arguments: " + e.getMessage(), e);
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

      // create tool call with message reference
      ToolCall toolCallWithMessage =
          ToolCall.create(
              user,
              message, // associate with the assistant message
              toolCall.getToolName(),
              toolCall.getArguments(),
              toolCall.getSource(),
              toolCall.getSourceCallId(),
              toolCall.getSourceMetadata());

      // save and update content
      ToolCall savedToolCall = toolCallRepository.save(toolCallWithMessage);
      updateToolContentWithId(toolContents.get(i), savedToolCall, outputContents);
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

  private List<ContentDto> buildMessageContents(
      ChatCompletion chatCompletion, List<ToolContentDto> toolContents) {
    List<ContentDto> contents = new ArrayList<>();

    // add text content if present
    String textContent = chatCompletion.choices().get(0).message().content().orElse("");
    if (!textContent.isEmpty()) {
      contents.add(new TextContentDto(textContent));
    }

    // add tool contents
    contents.addAll(toolContents);

    return contents;
  }

  private ChatCompletion callOpenAIWithContext(Conversation conversation, Model model) {
    List<MessageDto> contextMessages = getConversationContext(conversation.getId());
    return openAIService.chatCompletionWithTools(contextMessages, model);
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

  private void saveTokenUsage(
      User user, Message message, Model model, ChatCompletion chatCompletion) {
    if (chatCompletion.usage().isPresent()) {
      openAIService.saveTokenUsage(
          user, message, model, chatCompletion.usage().get(), "chat_completion_with_tools");
    }
  }

  private boolean shouldUpdateTitle(Integer messageIndex, boolean isNewConversation) {
    return messageIndex == 0 || isNewConversation;
  }

  private void updateConversationTitle(
      Conversation conversation, Message inputMessage, Message outputMessage, User user) {
    String inputText = extractTextContent(MessageDto.from(inputMessage));
    String outputText = extractTextContent(MessageDto.from(outputMessage));
    String title = generateConversationTitle(inputText, outputText, user, outputMessage);

    conversationService.updateTitle(conversation.getId(), title);
    conversation.update(title);
  }

  private String extractTextContent(MessageDto message) {
    return message.getContent().stream()
        .filter(content -> content instanceof TextContentDto)
        .map(content -> ((TextContentDto) content).getText())
        .findFirst()
        .orElse("");
  }

  private String generateConversationTitle(
      String inputText, String outputText, User user, Message message) {
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

    // clean up title
    title = title.replaceAll("^\"|\"$", "").trim();
    if (title.length() > TITLE_MAX_LENGTH) {
      title = title.substring(0, TITLE_MAX_LENGTH - 3) + "...";
    }

    return title;
  }

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

    validateMessageAccess(user.getId(), conversation.getUser().getId(), "access this conversation");
    return conversation;
  }

  private Message findParentMessage(UUID parentMessageId, UUID conversationId) {
    if (parentMessageId == null) {
      return null;
    }

    return messageRepository
        .findById(parentMessageId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent message not found"));
  }

  private void validateMessageAccess(Long userId, Long ownerId, String action) {
    if (!userId.equals(ownerId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, String.format("You do not have permission to %s", action));
    }
  }

  private Integer getNextMessageIndex(UUID conversationId) {
    return messageRepository
        .findTopByConversationIdOrderByIndexDesc(conversationId)
        .map(message -> message.getIndex() + 1)
        .orElse(0);
  }

  private MessageDto buildResponseDto(
      Message outputMessage, Conversation conversation, boolean isNewConversation) {
    MessageDto responseDto = MessageDto.from(outputMessage);

    if (isNewConversation) {
      responseDto.setConversation(ConversationDto.from(conversation));
    }

    return responseDto;
  }
}
