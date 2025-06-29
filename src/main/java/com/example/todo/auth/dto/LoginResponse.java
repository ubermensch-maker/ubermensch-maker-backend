package com.example.todo.auth.dto;

import com.example.todo.user.dto.UserDto;

public record LoginResponse(String token, UserDto user) {}
