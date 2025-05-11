package com.example.todo.user;

import com.example.todo.user.dto.UserCreateDto;
import com.example.todo.user.dto.UserDto;
import com.example.todo.user.dto.UserUpdateDto;
import com.example.todo.user.enums.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto create(UserCreateDto request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userRepository.save(
                User.create(request.getEmail(), encodedPassword, request.getName(), UserRole.USER, request.getTimezone())
        );
        return UserDto.from(user);
    }

    public UserDto read(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserDto.from(user);
    }

    public List<UserDto> list() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDto::from).toList();
    }

    @Transactional
    public UserDto update(Long userId, UserUpdateDto request) {
        User user = userRepository.findById(userId).orElseThrow();
        String encodedPassword = request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null;
        user.update(request.getEmail(), encodedPassword, request.getName(), request.getTimezone());
        return UserDto.from(user);
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
