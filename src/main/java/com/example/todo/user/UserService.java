package com.example.todo.user;

import com.example.todo.auth.dto.SignupRequest;
import com.example.todo.user.dto.UserDto;
import com.example.todo.user.dto.UserUpdateDto;
import com.example.todo.user.enums.UserRole;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
  }

  public UserDto create(SignupRequest request) {
    if (findByEmail(request.email()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    User user =
        User.create(
            request.email(),
            passwordEncoder.encode(request.password()),
            request.name(),
            UserRole.USER);

    userRepository.save(user);

    return UserDto.from(user);
  }

  public UserDto read(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return UserDto.from(user);
  }

  @Transactional
  public UserDto update(Long userId, UserUpdateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    String encodedPassword =
        request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null;

    user.update(request.getEmail(), encodedPassword, request.getName());

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
}
