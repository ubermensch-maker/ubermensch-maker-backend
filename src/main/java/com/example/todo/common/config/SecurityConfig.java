package com.example.todo.common.config;

import com.example.todo.auth.handler.OAuth2FailureHandler;
import com.example.todo.auth.handler.OAuth2SuccessHandler;
import com.example.todo.common.security.JwtAuthenticationFilter;
import com.example.todo.common.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtTokenProvider jwtTokenProvider;
  private final UrlBasedCorsConfigurationSource corsConfigurationSource;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final OAuth2FailureHandler oAuth2FailureHandler;

  public SecurityConfig(
      JwtTokenProvider jwtTokenProvider,
      UrlBasedCorsConfigurationSource corsConfigurationSource,
      OAuth2SuccessHandler oAuth2SuccessHandler,
      OAuth2FailureHandler oAuth2FailureHandler) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.corsConfigurationSource = corsConfigurationSource;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    this.oAuth2FailureHandler = oAuth2FailureHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/health",
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/auth/session/**",
                        "/auth/logout")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth2 ->
                oauth2.successHandler(oAuth2SuccessHandler).failureHandler(oAuth2FailureHandler))
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
