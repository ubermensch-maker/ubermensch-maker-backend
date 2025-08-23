package com.example.todo.message;

import com.example.todo.conversation.Conversation;
import com.example.todo.conversation.ConversationRepository;
import com.example.todo.conversation.ConversationService;
import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.message.dto.MessageCreateDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.MessageListDto;
import com.example.todo.message.dto.TextContentDto;
import com.example.todo.message.dto.ToolContentDto;
import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.message.enums.Model;
import com.example.todo.openai.OpenAIService;
import com.example.todo.toolcall.ToolCall;
import com.example.todo.toolcall.ToolCallRepository;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionMessageToolCall;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ConversationRepository conversationRepository;
  private final OpenAIService openAIService;
  private final ConversationService conversationService;
  private final ToolCallRepository toolCallRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public MessageDto create(Long userId, MessageCreateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    boolean isNewConversation = request.getConversationId() == null;
    Conversation conversation =
        isNewConversation
            ? conversationRepository.save(Conversation.create(user, "New Chat"))
            : conversationRepository
                .findById(request.getConversationId())
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Conversation not found"));

    Message parentMessage = null;
    if (request.getParentMessageId() != null) {
      parentMessage =
          messageRepository
              .findById(request.getParentMessageId())
              .orElseThrow(
                  () ->
                      new ResponseStatusException(
                          HttpStatus.NOT_FOUND, "Parent message not found"));
    }

    Integer messageIndex = getNextMessageIndex(conversation.getId());

    if (parentMessage == null && messageIndex > 0) {
      parentMessage =
          messageRepository
              .findTopByConversationIdAndIndexLessThanOrderByIndexDesc(
                  conversation.getId(), messageIndex)
              .orElse(null);
    }

    Message inputMessage =
        Message.create(
            user,
            conversation,
            parentMessage,
            messageIndex,
            request.getModel(),
            request.getRole(),
            request.getContent());
    messageRepository.save(inputMessage);

    int contextLimit = 10;
    Pageable pageable = PageRequest.of(0, contextLimit);
    List<Message> recentMessages =
        messageRepository.findByConversationIdOrderByIndexDesc(conversation.getId(), pageable);

    List<MessageDto> contextMessages =
        recentMessages.stream()
            .sorted((m1, m2) -> m1.getIndex().compareTo(m2.getIndex()))
            .map(MessageDto::from)
            .toList();

    ChatCompletion chatCompletion = openAIService.chatCompletionWithTools(contextMessages, request.getModel(), user, inputMessage);
    
    List<ToolContentDto> toolContents = new ArrayList<>();
    List<ToolCall> toolCalls = new ArrayList<>();
    
    for (ChatCompletion.Choice choice : chatCompletion.choices()) {
      if (choice.message().toolCalls().isPresent()) {
        for (ChatCompletionMessageToolCall toolCall : choice.message().toolCalls().get()) {
          Map<String, Object> arguments;
          try {
            // arguments() 결과를 안전하게 Map으로 변환
            String argumentsJson = toolCall.function().arguments().toString();
            arguments = objectMapper.readValue(argumentsJson, new TypeReference<Map<String, Object>>() {});
          } catch (Exception e) {
            throw new RuntimeException("Failed to parse tool call arguments: " + e.getMessage(), e);
          }
          
          ToolCall toolCallEntity = ToolCall.create(
              user,
              null, // will set after message is saved
              toolCall.function().name(),
              arguments,
              toolCall.id()
          );
          toolCalls.add(toolCallEntity);
          
          ToolContentDto toolContent = new ToolContentDto(
              toolCall.function().name(),
              arguments,
              null, // will set after toolCall is saved
              toolCallEntity.getStatus(),
              toolCall.id()
          );
          toolContents.add(toolContent);
        }
      }
    }
    
    String outputMessageText = chatCompletion.choices().get(0).message().content().orElse("");
    List<ContentDto> outputContents = new ArrayList<>();
    
    if (!outputMessageText.isEmpty()) {
      outputContents.add(new TextContentDto(outputMessageText));
    }
    outputContents.addAll(toolContents);

    Message outputMessage =
        Message.create(
            null,
            conversation,
            inputMessage,
            messageIndex + 1,
            request.getModel(),
            MessageRole.ASSISTANT,
            outputContents);
    messageRepository.save(outputMessage);
    
    // Update tool calls with message reference and save
    for (int i = 0; i < toolCalls.size(); i++) {
      ToolCall toolCall = toolCalls.get(i);
      toolCall = ToolCall.create(
          user,
          outputMessage,
          toolCall.getFunctionName(),
          toolCall.getArguments(),
          toolCall.getOpenaiToolCallId()
      );
      toolCallRepository.save(toolCall);
      
      // Update tool content with saved tool call ID
      ToolContentDto originalToolContent = toolContents.get(i);
      ToolContentDto updatedToolContent = new ToolContentDto(
          originalToolContent.getName(),
          originalToolContent.getArgs(),
          toolCall.getId(),
          toolCall.getStatus(),
          originalToolContent.getOpenaiToolCallId()
      );
      outputContents.set(outputContents.indexOf(originalToolContent), updatedToolContent);
    }

    if (messageIndex == 0 || isNewConversation) {
      MessageDto inputMessageDto = MessageDto.from(inputMessage);
      String conversationTitle = generateConversationTitle(inputMessageDto, outputMessageText, user, outputMessage);
      conversationService.updateTitle(conversation.getId(), conversationTitle);
      conversation.update(conversationTitle);
    }

    MessageDto responseDto = MessageDto.from(outputMessage);
    if (isNewConversation) {
      responseDto.setConversation(ConversationDto.from(conversation));
    }

    return responseDto;
  }

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

    // TODO(jiyoung): add pagination
    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    List<Message> messages = messageRepository.findAllByConversationId(conversationId, sort);

    return new MessageListDto(messages.size(), messages.stream().map(MessageDto::from).toList());
  }

  private Integer getNextMessageIndex(UUID conversationId) {
    return messageRepository
        .findTopByConversationIdOrderByIndexDesc(conversationId)
        .map(message -> message.getIndex() + 1)
        .orElse(0);
  }

  private String generateConversationTitle(MessageDto inputMessage, String outputMessage, User user, Message message) {
    String inputText =
        inputMessage.getContent().stream()
            .filter(content -> content instanceof TextContentDto)
            .map(content -> ((TextContentDto) content).getText())
            .findFirst()
            .orElse("");

    String prompt =
        String.format(
            "Generate a short title that summarizes the main topic (max 50"
                + " characters):\n"
                + "User: %s\n"
                + "Assistant: %s\n\n"
                + "Title:",
            inputText,
            outputMessage.length() > 200 ? outputMessage.substring(0, 200) + "..." : outputMessage);

    MessageDto systemMessage = new MessageDto();
    systemMessage.setRole(MessageRole.SYSTEM);
    systemMessage.setContent(List.of(new TextContentDto(prompt)));

    List<MessageDto> messages = List.of(systemMessage);
    String title = openAIService.chatCompletion(messages, Model.GPT_4_1_NANO, user, message);

    title = title.replaceAll("^\"|\"$", "").trim();

    if (title.length() > 50) {
      title = title.substring(0, 47) + "...";
    }

    return title;
  }

  @Transactional
  public MessageDto processToolResult(Long userId, ToolCall toolCall, String toolResultMessage) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Conversation conversation = toolCall.getMessage().getConversation();
    Message originalMessage = toolCall.getMessage();
    
    // Create a message with the tool result summary
    Integer messageIndex = getNextMessageIndex(conversation.getId());
    Message toolResultMsg = Message.create(
        user,
        conversation,
        originalMessage,
        messageIndex,
        Model.GPT_4_1_NANO,
        MessageRole.USER,
        List.of(new TextContentDto(toolResultMessage))
    );
    messageRepository.save(toolResultMsg);

    // Get recent messages for context
    int contextLimit = 10;
    Pageable pageable = PageRequest.of(0, contextLimit);
    List<Message> recentMessages =
        messageRepository.findByConversationIdOrderByIndexDesc(conversation.getId(), pageable);

    List<MessageDto> contextMessages =
        recentMessages.stream()
            .sorted((m1, m2) -> m1.getIndex().compareTo(m2.getIndex()))
            .map(MessageDto::from)
            .toList();

    // Send to OpenAI with tools for response
    ChatCompletion chatCompletion = openAIService.chatCompletionWithTools(contextMessages, Model.GPT_4_1_NANO, user, toolResultMsg);
    
    List<ToolContentDto> toolContents = new ArrayList<>();
    List<ToolCall> toolCalls = new ArrayList<>();
    
    for (ChatCompletion.Choice choice : chatCompletion.choices()) {
      if (choice.message().toolCalls().isPresent()) {
        for (ChatCompletionMessageToolCall toolCallMsg : choice.message().toolCalls().get()) {
          Map<String, Object> arguments;
          try {
            arguments = objectMapper.readValue(
                toolCallMsg.function().arguments(),
                new TypeReference<Map<String, Object>>() {}
            );
          } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse tool arguments", e);
          }

          ToolCall newToolCall = ToolCall.create(
              user,
              toolResultMsg,
              toolCallMsg.function().name(),
              arguments,
              toolCallMsg.id()
          );
          toolCallRepository.save(newToolCall);
          toolCalls.add(newToolCall);

          ToolContentDto toolContent = new ToolContentDto(
              newToolCall.getFunctionName(),
              arguments,
              newToolCall.getId(), // will set after toolCall is saved
              newToolCall.getStatus(),
              newToolCall.getOpenaiToolCallId()
          );
          toolContents.add(toolContent);
        }
      }
    }

    String textContent = chatCompletion.choices().get(0).message().content().orElse("");
    List<ContentDto> content = new ArrayList<>();
    if (!textContent.isEmpty()) {
      content.add(new TextContentDto(textContent));
    }
    content.addAll(toolContents);

    // Create assistant response message with tool calls
    Integer responseIndex = getNextMessageIndex(conversation.getId());
    Message assistantResponse = Message.create(
        user,
        conversation,
        toolResultMsg,
        responseIndex,
        Model.GPT_4_1_NANO,
        MessageRole.ASSISTANT,
        content
    );
    messageRepository.save(assistantResponse);

    return MessageDto.from(assistantResponse);
  }

  @Transactional
  public void delete(Long userId, UUID messageId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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
}
