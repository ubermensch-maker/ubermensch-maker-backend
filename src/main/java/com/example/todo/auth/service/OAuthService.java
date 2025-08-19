package com.example.todo.auth.service;

import com.example.todo.user.UserService;
import com.example.todo.user.dto.UserDto;
import com.example.todo.user.enums.OAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

  private final UserService userService;

  public UserDto processOAuth2User(OAuth2User oauth2User) {
    // Google OAuth2User에서 정보 추출
    String email = oauth2User.getAttribute("email");
    String name = oauth2User.getAttribute("name");
    String picture = oauth2User.getAttribute("picture");
    String providerId = oauth2User.getAttribute("sub");

    if (email == null || providerId == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Required OAuth information missing");
    }

    log.info("Processing OAuth2 user: email={}, name={}", email, name);

    // 사용자 조회 또는 생성
    return getOrCreateUser(email, name, picture, OAuthProvider.GOOGLE, providerId);
  }

  private UserDto getOrCreateUser(
      String email, String name, String picture, OAuthProvider provider, String providerId) {
    try {
      // 기존 사용자 조회
      return userService.getByEmail(email);
    } catch (ResponseStatusException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        // 사용자가 없으면 새로 생성
        log.info("Creating new OAuth user: email={}, provider={}", email, provider);
        return userService.createFromOAuth(email, name, picture, provider, providerId);
      }
      throw e;
    }
  }
}
