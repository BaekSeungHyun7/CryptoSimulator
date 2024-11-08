package com.baeksh.cryptoSimulator.controller;

import com.baeksh.cryptoSimulator.dto.UserDto;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.baeksh.cryptoSimulator.util.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.repository.UserRepository;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;

  // JWT에서 userId를 추출하고 요청의 userId와 일치하는지 검증하는 메서드
  private void validateUserAccess(Long requestUserId, Authentication authentication) {
    if (authentication == null) {
      authentication = SecurityContextHolder.getContext().getAuthentication();
    }
    
    if (authentication == null || authentication.getName() == null) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    Long authUserId;
    try {
      authUserId = Long.parseLong(authentication.getName());
    } catch (NumberFormatException e) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    if (!Objects.equals(authUserId, requestUserId)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<UserEntity> register(@RequestBody UserDto.SignUp request) {
    return ResponseEntity.ok(userService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody UserDto.Login request) {
    return ResponseEntity.ok(userService.login(request));
  }

  @PutMapping("/{userId}")
  public ResponseEntity<UserEntity> updateUser(@PathVariable Long userId, @RequestBody UserDto.Update request) {
    validateUserAccess(userId, SecurityContextHolder.getContext().getAuthentication());
    return ResponseEntity.ok(userService.updateUser(userId, request));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserEntity> getUser(@PathVariable("userId") Long userId) {
    validateUserAccess(userId, SecurityContextHolder.getContext().getAuthentication());
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    return ResponseEntity.ok(user);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    validateUserAccess(userId, SecurityContextHolder.getContext().getAuthentication());
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}
