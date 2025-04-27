package com.example.todo.service;

import com.example.todo.dto.request.UserCreateRequest;
import com.example.todo.dto.request.UserUpdateRequest;
import com.example.todo.dto.response.UserResponse;
import com.example.todo.entity.User;
import com.example.todo.entity.UserRole;
import com.example.todo.repository.UserRepository;
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
    public UserResponse create(UserCreateRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userRepository.save(
                User.create(request.getEmail(), encodedPassword, request.getName(), UserRole.USER, request.getTimezone())
        );
        return UserResponse.from(user);
    }

    public UserResponse read(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserResponse.from(user);
    }

    public List<UserResponse> list() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse update(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        String encodedPassword = request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null;
        user.update(request.getEmail(), encodedPassword, request.getName(), request.getTimezone());
        return UserResponse.from(user);
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
