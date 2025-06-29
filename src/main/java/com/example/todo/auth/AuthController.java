package com.example.todo.auth;

import com.example.todo.auth.dto.LoginRequest;
import com.example.todo.auth.dto.LoginResponse;
import com.example.todo.auth.dto.SignupRequest;
import com.example.todo.common.security.JwtTokenProvider;
import com.example.todo.user.UserService;
import com.example.todo.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;

  @PostMapping("/auth/signup")
  public ResponseEntity<UserDto> signup(@RequestBody SignupRequest request) {
    UserDto user = userService.create(request);

    return ResponseEntity.ok(user);
  }

  @PostMapping("/auth/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    String token =
        jwtTokenProvider.createToken(
            request.email(),
            auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

    UserDto user = userService.getByEmail(request.email());

    return ResponseEntity.ok(new LoginResponse(token, user));
  }
}
