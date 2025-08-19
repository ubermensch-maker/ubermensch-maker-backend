package com.example.todo.message;

import com.example.todo.conversation.Conversation;
import com.example.todo.conversation.ConversationRepository;
import com.example.todo.conversation.ConversationService;
import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.message.dto.MessageCreateDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.MessageListDto;
import com.example.todo.message.dto.TextContentDto;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.message.enums.Model;
import com.example.todo.openai.OpenAIService;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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

    String outputMessageText = openAIService.chatCompletion(contextMessages, request.getModel());

    Message outputMessage =
        Message.create(
            null,
            conversation,
            inputMessage,
            messageIndex + 1,
            request.getModel(),
            MessageRole.ASSISTANT,
            List.of(new TextContentDto(outputMessageText)));
    messageRepository.save(outputMessage);

    if (messageIndex == 0 || isNewConversation) {
      MessageDto inputMessageDto = MessageDto.from(inputMessage);
      String conversationTitle = generateConversationTitle(inputMessageDto, outputMessageText);
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

  private String generateConversationTitle(MessageDto inputMessage, String outputMessage) {
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
    String title = openAIService.chatCompletion(messages, Model.GPT_4_1_NANO);

    title = title.replaceAll("^\"|\"$", "").trim();

    if (title.length() > 50) {
      title = title.substring(0, 47) + "...";
    }

    return title;
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
