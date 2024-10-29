package com.baeksh.cryptoSimulator.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import javax.xml.bind.DatatypeConverter;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey; // 비밀키 - application.properties 파일에서 주입 (gitignore 처리)

    private static final long VALIDITY_IN_MS = 3600000;  // 1시간 유효기간

    /**
     * 사용자 이름과 역할을 기반으로 JWT 토큰을 생성
     *
     * @param username 사용자 이름
     * @param role 사용자 역할
     * @return 생성된 JWT 토큰 문자열
     */
    public String createToken(String username, String role) {
        // 클레임 생성 및 사용자 정보 추가
        Claims claims = Jwts.claims().setSubject(username);
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
    
    
    //토큰 -> userId를 추출하는 메서드
    public Long getUserIdFromToken(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.printBase64Binary(secretKey.getBytes()))
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
    
    
    
}