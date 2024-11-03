package com.baeksh.cryptoSimulator.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
  @Value("${jwt.secret}")
  private String secretKey; // 비밀키 - 환경변수처리

  private static final long VALIDITY_IN_MS = 3600000;  // 1시간 유효기간

  /**
   * 사용자 이름과 역할을 기반으로 JWT 토큰을 생성
   *
   * @param username 사용자 이름
   * @param role 사용자 역할
   * @return 생성된 JWT 토큰 문자열
   */
  public String createToken(Long userId, String role) {
    // 클레임 생성 및 사용자 정보 추가
    Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
    // 사용자 역할 정보를 클레임에 추가
    claims.put("role", role);

    // 현재 시간 설정
    Date now = new Date();
    // 토큰 만료 시간 설정
    Date validity = new Date(now.getTime() + VALIDITY_IN_MS);

    // 비밀 키를 Base64로 인코딩
    String base64SecretKey = DatatypeConverter.printBase64Binary(secretKey.getBytes());
    // JWT 토큰 생성
    return Jwts.builder()
      .setClaims(claims)
      .setIssuedAt(now)
      .setExpiration(validity)
      .signWith(SignatureAlgorithm.HS256, base64SecretKey)
      .compact();
  }

  /**
   * 토큰에서 사용자 이름을 추출
   *
   * @param token JWT 토큰
   * @return 사용자 이름
   */
  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parser()
      .setSigningKey(DatatypeConverter.printBase64Binary(secretKey.getBytes()))
      .parseClaimsJws(token)
      .getBody();
    return Long.parseLong(claims.getSubject());
  }

  /**
   * 토큰에서 권한을 추출
   *
   * @param token JWT 토큰
   * @return 권한 목록
   */
  public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
    Claims claims = Jwts.parser()
      .setSigningKey(DatatypeConverter.printBase64Binary(secretKey.getBytes()))
      .parseClaimsJws(token)
      .getBody();
    String role = (String) claims.get("role");
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    if (StringUtils.hasText(role)) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    }
    return authorities;
  }

  /**
   * 토큰 유효성 검사
   *
   * @param token JWT 토큰
   * @return 유효한 경우 true, 그렇지 않으면 false
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
        .setSigningKey(DatatypeConverter.printBase64Binary(secretKey.getBytes()))
        .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 토큰에서 userId를 추출하는 메서드
   *
   * @param authentication Authentication
   * @return userId
   */
  public Long getUserIdFromToken(Authentication authentication) {
    String token = authentication.getCredentials().toString();
    Claims claims = Jwts.parser()
      .setSigningKey(DatatypeConverter.printBase64Binary(secretKey.getBytes()))
      .parseClaimsJws(token)
      .getBody();
    return Long.parseLong(claims.getSubject());
  }
}
