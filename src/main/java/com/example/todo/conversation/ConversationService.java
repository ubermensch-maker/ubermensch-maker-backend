package com.example.todo.conversation;

import com.example.todo.conversation.dto.ConversationCreateDto;
import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.conversation.dto.ConversationListDto;
import com.example.todo.conversation.dto.ConversationUpdateDto;
import com.example.todo.goal.Goal;
import com.example.todo.goal.GoalRepository;
import com.example.todo.message.MessageRepository;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final GoalRepository goalRepository;
  private final MessageRepository messageRepository;

  @Transactional
  public ConversationDto create(Long userId, ConversationCreateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Goal goal =
        goalRepository
            .findById(request.getGoalId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

    Conversation conversation = Conversation.create(user, goal, request.getTitle());

    return ConversationDto.from(conversationRepository.save(conversation));
  }

  public ConversationDto read(Long conversationId) {
    Conversation conversation =
        conversationRepository
            .findById(conversationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

    return ConversationDto.from(conversation);
  }

  public ConversationListDto list(Long userId, Long goalId) {
    List<Conversation> conversations;

    if (goalId != null) {
      conversations = conversationRepository.findAllByUserIdAndGoalId(userId, goalId);
    } else {
      conversations = conversationRepository.findAllByUserId(userId);
    }

    return new ConversationListDto(
        conversations.size(), conversations.stream().map(ConversationDto::from).toList());
  }

  @Transactional
  public ConversationDto update(Long userId, Long conversationId, ConversationUpdateDto request) {
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
  public void delete(Long userId, Long conversationId) {
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
