package com.example.todo.conversation;

import com.example.todo.conversation.dto.ConversationCreateDto;
import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.conversation.dto.ConversationListDto;
import com.example.todo.conversation.dto.ConversationUpdateDto;
import com.example.todo.message.MessageRepository;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;

  @Transactional
  public ConversationDto create(Long userId, ConversationCreateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Conversation conversation = Conversation.create(user, request.getTitle());

    return ConversationDto.from(conversationRepository.save(conversation));
  }

  public ConversationDto read(Long userId, UUID conversationId) {
    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    if (!userId.equals(conversation.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to read this conversation");
    }

    return ConversationDto.from(conversation);
  }

  public ConversationListDto list(Long userId) {
    List<Conversation> conversations;

    conversations = conversationRepository.findAllByUserId(userId);

    return new ConversationListDto(
        conversations.size(), conversations.stream().map(ConversationDto::from).toList());
  }

  @Transactional
  public ConversationDto update(Long userId, UUID conversationId, ConversationUpdateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    if (!user.getId().equals(conversation.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to update this conversation");
    }

    conversation.update(request.getTitle());

    return ConversationDto.from(conversation);
  }

  @Transactional
  public void delete(Long userId, UUID conversationId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    if (!user.getId().equals(conversation.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to delete this conversation");
    }

    messageRepository.deleteAllByConversationId(conversationId);
    conversationRepository.delete(conversation);
  }
}
