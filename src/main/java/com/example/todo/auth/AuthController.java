package com.example.todo.auth;

import com.example.todo.auth.dto.DevLoginRequest;
import com.example.todo.auth.dto.JwtResponse;
import com.example.todo.auth.service.AuthSessionService;
import com.example.todo.common.security.JwtTokenProvider;
import com.example.todo.user.UserService;
import com.example.todo.user.dto.UserDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;
  private final AuthSessionService authSessionService;
  private final Environment environment;

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
          JwtResponse.of(token, jwtTokenProvider.getValidityInMs() / 1000, user));
    } catch (Exception e) {
      log.error("Error exchanging session", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to exchange session"));
    }
  }

  @GetMapping("/auth/me")
  public ResponseEntity<?> getCurrentUser(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    try {
      // Spring Security에서 인증된 사용자 정보 사용
      UserDto user = userService.getByEmail(principal.getUsername());

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

  @PostMapping("/auth/dev/login")
  public ResponseEntity<?> devLogin(@Valid @RequestBody DevLoginRequest request) {
    try {
      // 개발 환경에서만 허용 (local, dev 프로파일)
      if (!environment.acceptsProfiles(Profiles.of("local", "dev"))) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", "Dev login is only available in development environments"));
      }

      UserDto user = userService.getByEmail(request.email());

      // JWT 토큰 생성
      String token = jwtTokenProvider.createToken(request.email(), List.of("USER"));

      return ResponseEntity.ok(
          JwtResponse.of(token, jwtTokenProvider.getValidityInMs() / 1000, user));

    } catch (Exception e) {
      log.error("Error in dev login", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Login failed: " + e.getMessage()));
    }
  }
}
