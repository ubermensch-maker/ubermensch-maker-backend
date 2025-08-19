package com.example.todo.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    // Authorization 헤더에서 Bearer 토큰 추출
    String header = req.getHeader("Authorization");
    String token = null;

    if (header != null && header.startsWith("Bearer ")) {
      token = header.substring(7);
    }

    if (token != null && jwtTokenProvider.validateToken(token)) {
      Authentication auth = jwtTokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    chain.doFilter(req, res);
  }
}
