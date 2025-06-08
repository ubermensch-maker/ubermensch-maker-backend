package com.example.todo.user;

import com.example.todo.user.dto.UserCreateDto;
import com.example.todo.user.dto.UserDto;
import com.example.todo.user.dto.UserUpdateDto;
import com.example.todo.user.enums.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto create(UserCreateDto request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = userRepository.save(
                User.create(
                        request.getEmail(),
                        encodedPassword,
                        request.getName(),
                        UserRole.USER
                )
        );

        return UserDto.from(user);
    }

    public UserDto read(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserDto.from(user);
    }

    @Transactional
    public UserDto update(Long userId, UserUpdateDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String encodedPassword = request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null;

        user.update(
                request.getEmail(),
                encodedPassword,
                request.getName()
        );

        return UserDto.from(user);
    }

    @Transactional
    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.delete(user);
    }
}
