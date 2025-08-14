package com.example.todo.auth.handler;

import com.example.todo.auth.OAuthService;
import com.example.todo.common.security.JwtTokenProvider;
import com.example.todo.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthService oAuthService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

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
            String token = jwtTokenProvider.createToken(
                user.getEmail(),
                authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()
            );

            // HttpOnly 쿠키로 토큰 설정 (보안 표준)
            Cookie tokenCookie = new Cookie("auth-token", token);
            tokenCookie.setHttpOnly(true);  // XSS 공격 방지
            tokenCookie.setSecure(request.isSecure()); // HTTPS면 true, HTTP면 false
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(86400); // 24시간
            response.addCookie(tokenCookie);

            // API 응답으로 성공 여부와 사용자 정보 반환
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> successResponse = Map.of(
                "success", true,
                "message", "로그인 성공",
                "user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail()
                )
            );

            objectMapper.writeValue(response.getWriter(), successResponse);
            log.info("OAuth success response sent");

        } catch (Exception e) {
            log.error("Error processing OAuth success", e);
            sendErrorResponse(response, "OAuth 처리 중 오류가 발생했습니다.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = Map.of(
            "success", false,
            "error", message,
            "timestamp", System.currentTimeMillis()
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}