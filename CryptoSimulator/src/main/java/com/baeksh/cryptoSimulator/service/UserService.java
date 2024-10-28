package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import com.baeksh.cryptoSimulator.dto.UserDto;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public UserEntity register(UserDto.SignUp request) {
        // 사용자 이름이 이미 존재하는지 확인
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        // 새로운 사용자 생성
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))  // 비밀번호 암호화
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        return userRepository.save(user);
    }

    public String login(UserDto.Login request) {
        // 사용자 인증 시도
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 사용자 조회
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // JWT 토큰 생성 및 반환
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole());
    }

    public UserEntity updateUser(Long userId, UserDto.Update request) {
        // 사용자 ID로 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 사용자 정보 업데이트
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // 비밀번호 암호화 후 저장
        return userRepository.save(user);
    }

    public UserEntity getUser(Long userId) {
        // 사용자 ID로 사용자 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public void deleteUser(Long userId) {
        // 사용자 존재 여부 확인 후 삭제
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}

