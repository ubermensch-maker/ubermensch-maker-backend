package com.example.todo.auth.dto;

import com.example.todo.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") long expiresIn,
    UserDto user) {

  public static JwtResponse of(String token, long expiresInSeconds, UserDto user) {
    return new JwtResponse(token, "Bearer", expiresInSeconds, user);
  }
}
