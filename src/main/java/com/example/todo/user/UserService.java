package com.example.todo.user;

import com.example.todo.user.dto.UserDto;
import com.example.todo.user.enums.OAuthProvider;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public UserDto get(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return UserDto.from(user);
  }

  @Transactional
  public void delete(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    userRepository.delete(user);
  }

  public Optional<UserDto> findByEmail(String email) {
    return userRepository.findByEmail(email).map(UserDto::from);
  }

  public UserDto getByEmail(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return UserDto.from(user);
  }

  @Transactional
  public UserDto createFromOAuth(
      String email, String name, String picture, OAuthProvider provider, String providerId) {
    if (findByEmail(email).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    User user = User.createFromOAuth(email, name, picture, provider, providerId);
    userRepository.save(user);
    return UserDto.from(user);
  }

  @Transactional
  public UserDto updateProfile(Long userId, String name) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    user.updateProfile(name);
    return UserDto.from(user);
  }
}
