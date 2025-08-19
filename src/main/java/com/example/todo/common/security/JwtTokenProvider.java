package com.example.todo.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  @Value("${jwt.secret}")
  private String secretKeyString;

  @Value("${jwt.expiration-ms}")
  private long validityInMs;

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createToken(String email, List<String> roles) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + validityInMs);

    return Jwts.builder()
        .subject(email)
        .claim("roles", roles)
        .issuedAt(now)
        .expiration(exp)
        .signWith(secretKey, Jwts.SIG.HS256)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails =
        new org.springframework.security.core.userdetails.User(
            getEmail(token),
            "",
            getRoles(token).stream().map(SimpleGrantedAuthority::new).toList());
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getEmail(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  @SuppressWarnings("unchecked")
  private List<String> getRoles(String token) {
    return (List<String>)
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("roles");
  }

  public long getValidityInMs() {
    return validityInMs;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
