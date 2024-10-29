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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    //회원가입
    public UserEntity register(UserDto.SignUp request) {
      // 사용자 중복 확인
      if (userRepository.existsByUsername(request.getUsername())){
        throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
      // 사용자 이메일 중복 확인
      if (userRepository.existsByEmail(request.getEmail())) {
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

    // 로그인 & JWT 반환
    public String login(UserDto.Login request) {
        // 사용자 인증 시도
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 사용자 조회
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // JWT 토큰 생성 및 반환
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole());
    }

    //사용자 정보 업데이트
    public UserEntity updateUser(Long userId, UserDto.Update request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // 비밀번호 암호화 후 저장
        return userRepository.save(user);
    }

    // 사용자 ID로 사용자 조회
    public UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 사용자 존재 여부 확인 후 삭제
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}

