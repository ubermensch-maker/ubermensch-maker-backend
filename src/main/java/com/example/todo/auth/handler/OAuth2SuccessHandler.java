package com.example.todo.auth.handler;

import com.example.todo.auth.OAuthService;
import com.example.todo.auth.service.AuthSessionService;
import com.example.todo.common.security.JwtTokenProvider;
import com.example.todo.user.dto.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final OAuthService oAuthService;
  private final AuthSessionService authSessionService;

  @Value("${app.frontend-url}")
  private String frontendUrl;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    log.info("OAuth2 authentication success");

    if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
      log.error("Authentication principal is not OAuth2User");
      sendErrorResponse(response, "Invalid authentication principal");
      return;
    }

    try {
      // OAuth2User에서 사용자 정보 추출 및 처리
      UserDto user = oAuthService.processOAuth2User(oauth2User);

      // JWT 토큰 생성
      String token =
          jwtTokenProvider.createToken(
              user.getEmail(),
              authentication.getAuthorities().stream()
                  .map(GrantedAuthority::getAuthority)
                  .toList());

      // 임시 세션 생성
      String sessionId = authSessionService.createSession(token);

      // 프론트엔드로 세션 ID와 함께 리다이렉트
      String redirectUrl = frontendUrl + "/auth/callback?session=" + sessionId;
      response.sendRedirect(redirectUrl);
      log.info("OAuth success - redirecting to: {} with session: {}", redirectUrl, sessionId);

    } catch (Exception e) {
      log.error("Error processing OAuth success", e);
      sendErrorResponse(response, "OAuth 처리 중 오류가 발생했습니다.");
    }
  }

  private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
    // 에러 발생시 프론트엔드 에러 페이지로 리다이렉트 (에러 메시지 노출 안함)
    String errorUrl = frontendUrl + "/auth/error";
    response.sendRedirect(errorUrl);
    log.error("OAuth error - redirecting to error page: {}, error: {}", errorUrl, message);
  }
}