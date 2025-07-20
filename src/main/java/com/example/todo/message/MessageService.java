package com.example.todo.message;

import com.example.todo.conversation.Conversation;
import com.example.todo.conversation.ConversationRepository;
import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.dto.MessageCreateDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.MessageListDto;
import com.example.todo.message.enums.ContentType;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.quest.QuestRepository;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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
  private final QuestRepository questRepository;

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

    Message inputMessage =
        Message.create(
            user, conversation, request.getModel(), request.getRole(), request.getContent());
    messageRepository.save(inputMessage);

    // TODO(jiyoung): call openai api and get ai response
    Message outputMessage =
        Message.create(
            null,
            conversation,
            request.getModel(),
            MessageRole.ASSISTANT,
            List.of(new ContentDto(ContentType.TEXT, "This is dummy response")));
    messageRepository.save(outputMessage);

    return MessageDto.from(outputMessage);
  }

  public MessageDto read(Long messageId) {
    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

    return MessageDto.from(message);
  }

  public MessageListDto list(Long userId, Long conversationId) {
    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    if (!userId.equals(conversation.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to see this message list");
    }

    // TODO(jiyoung): add pagination
    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    List<Message> messages = messageRepository.findAllByConversationId(conversationId, sort);

    return new MessageListDto(messages.size(), messages.stream().map(MessageDto::from).toList());
  }

  @Transactional
  public void delete(Long userId, Long messageId) {
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
