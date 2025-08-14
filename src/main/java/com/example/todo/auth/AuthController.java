package com.example.todo.auth;

import com.example.todo.common.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
  
  private final JwtTokenProvider jwtTokenProvider;


  @GetMapping("/api/auth/me")
  public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
    try {
      Cookie[] cookies = request.getCookies();
      if (cookies == null) {
        return ResponseEntity.status(401).body(Map.of("error", "No authentication cookies found"));
      }

      String token = null;

      for (Cookie cookie : cookies) {
        if ("auth-token".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }

      if (token == null) {
        return ResponseEntity.status(401).body(Map.of("error", "Authentication token not found"));
      }

      if (!jwtTokenProvider.validateToken(token)) {
        return ResponseEntity.status(401).body(Map.of("error", "Invalid authentication token"));
      }

      String email = jwtTokenProvider.getEmail(token);
      return ResponseEntity.ok(Map.of(
        "authenticated", true,
        "email", email
      ));

    } catch (Exception e) {
      log.error("Error checking authentication status", e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  @PostMapping("/api/auth/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    try {
      Cookie tokenCookie = new Cookie("auth-token", "");
      tokenCookie.setHttpOnly(true);
      tokenCookie.setSecure(false);
      tokenCookie.setPath("/");
      tokenCookie.setMaxAge(0);
      response.addCookie(tokenCookie);

      return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
    } catch (Exception e) {
      log.error("Error during logout", e);
      return ResponseEntity.status(500).body(Map.of("error", "Logout failed"));
    }
  }
}
