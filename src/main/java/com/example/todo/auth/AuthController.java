package com.example.todo.auth;

import com.example.todo.auth.dto.JwtResponse;
import com.example.todo.auth.dto.LoginRequest;
import com.example.todo.auth.dto.SignupRequest;
import com.example.todo.common.security.JwtTokenProvider;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import com.example.todo.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/auth/signup")
  public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
    }
    User user =
        User.create(
            request.email(),
            passwordEncoder.encode(request.password()),
            request.name(),
            UserRole.USER);
    userRepository.save(user);
    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/auth/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    String token =
        jwtTokenProvider.createToken(
            request.email(),
            auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    return ResponseEntity.ok(new JwtResponse(token));
  }
}
