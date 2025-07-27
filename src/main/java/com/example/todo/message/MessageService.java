package com.example.todo.message;

import com.example.todo.conversation.Conversation;
import com.example.todo.conversation.ConversationRepository;
import com.example.todo.message.dto.MessageCreateDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.MessageListDto;
import com.example.todo.message.dto.TextContentDto;
import com.example.todo.message.dto.ToolContentDto;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

  @Transactional
  public MessageDto create(Long userId, MessageCreateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Conversation conversation =
        conversationRepository
            .findById(request.getConversationId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

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

    Integer userMessageIndex = getNextMessageIndex(request.getConversationId());
    Message inputMessage =
        Message.create(
            user,
            conversation,
            parentMessage,
            userMessageIndex,
            request.getModel(),
            request.getRole(),
            request.getContent());
    messageRepository.save(inputMessage);

    // TODO(jiyoung): replace with actual assistant response
    Integer assistantMessageIndex = getNextMessageIndex(request.getConversationId());
    Message outputMessage =
        Message.create(
            null,
            conversation,
            inputMessage,
            assistantMessageIndex,
            request.getModel(),
            MessageRole.ASSISTANT,
            List.of(
                new TextContentDto(
                    "I can create that goal for you. Please review the details and confirm."),
                new ToolContentDto(
                    "create_goal",
                    Map.of(
                        "title", "Learn Next.js",
                        "description", "Become proficient with the latest features of Next.js"))));
    messageRepository.save(outputMessage);

    return MessageDto.from(outputMessage);
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
