package com.example.todo.auth;

import com.example.todo.auth.service.AuthSessionService;
import com.example.todo.common.security.JwtTokenProvider;
import com.example.todo.user.UserService;
import com.example.todo.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;
  private final AuthSessionService authSessionService;

  @GetMapping("/auth/session/{sessionId}")
  public ResponseEntity<?> exchangeSession(@PathVariable String sessionId) {
    try {
      String token = authSessionService.getTokenAndRemove(sessionId);

      if (token == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "Invalid or expired session"));
      }

      // 토큰에서 사용자 정보 추출
      String email = jwtTokenProvider.getEmail(token);
      UserDto user = userService.getByEmail(email);

      return ResponseEntity.ok(
          Map.of(
              "access_token",
              token,
              "token_type",
              "Bearer",
              "expires_in",
              86400, // 24시간
              "user",
              Map.of(
                  "id", user.getId(),
                  "name", user.getName(),
                  "email", user.getEmail())));
    } catch (Exception e) {
      log.error("Error exchanging session", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to exchange session"));
    }
  }

  @GetMapping("/auth/me")
  public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
    try {
      // Authorization 헤더에서 Bearer 토큰 추출
      String authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "No Bearer token found"));
      }

      String token = authHeader.substring(7);

      if (!jwtTokenProvider.validateToken(token)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid authentication token"));
      }

      String email = jwtTokenProvider.getEmail(token);

      // 사용자 정보 조회
      UserDto user = userService.getByEmail(email);

      return ResponseEntity.ok(
          Map.of(
              "authenticated",
              true,
              "user",
              Map.of(
                  "id", user.getId(),
                  "name", user.getName(),
                  "email", user.getEmail())));

    } catch (Exception e) {
      log.error("Error checking authentication status", e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  @PostMapping("/auth/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    // Bearer 토큰 방식에서는 클라이언트가 토큰을 삭제하면 됨
    return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
  }
}
