package com.example.todo.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private final Instant timestamp;
  private final int status;
  private final String error;
  private final String message;
  private final String path;
  private final Map<String, String> errors;

  public static ErrorResponse of(int status, String error, String message, String path) {
    return ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(status)
        .error(error)
        .message(message)
        .path(path)
        .build();
  }

  public static ErrorResponse withErrors(
      int status, String error, String message, String path, Map<String, String> errors) {
    return ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(status)
        .error(error)
        .message(message)
        .path(path)
        .errors(errors)
        .build();
  }
}
