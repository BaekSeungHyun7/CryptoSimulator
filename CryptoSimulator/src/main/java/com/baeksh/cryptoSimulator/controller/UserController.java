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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  
  private final UserService userService;
  private final UserRepository userRepository;
  
  // JWT에서 userId 추출하고 요청의 userId와 일치하는지 검증하는 메서드
  private void validateUserAccess(Long requestUserId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long tokenUserId = Long.valueOf(authentication.getName());
    if (!tokenUserId.equals(requestUserId)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);    
    }   
  }
  
  /**
   *  회원가입 요청을 처리하는 POST API 엔드포인트
   * @param request 회원가입 요청 정보 (username, password, email, phone, role)
   * @return 등록된 사용자 정보
  */
  @PostMapping("/signup")
  public ResponseEntity<UserEntity> register(@RequestBody UserDto.SignUp request) {
    return ResponseEntity.ok(userService.register(request));  
  }

  
  /**
   * 로그인 요청을 처리하는 POST API 엔드포인트
   *
   * @param request 로그인 요청 정보 (username, password)
   * @return JWT 토큰 문자열
  */
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody UserDto.Login request) {
    return ResponseEntity.ok(userService.login(request));
  }

  /**
   * 사용자 정보를 수정하는 PUT API 엔드포인트
   * @param userId 수정할 사용자 ID
   * @param request 사용자 수정 요청 정보 (password, email, phone)
   * @return 수정된 사용자 정보
   */
  @PutMapping("/{userId}")
  public ResponseEntity<UserEntity> updateUser(@PathVariable Long userId, @RequestBody UserDto.Update request) {
    validateUserAccess(userId);  
    return ResponseEntity.ok(userService.updateUser(userId, request));        
  }
  
  /**
   * 사용자 정보를 조회하는 GET API 엔드포인트
   *
   * @param userId 조회할 사용자 ID
   * @return 조회된 사용자 정보
   */
  @GetMapping("/{userId}")
  public ResponseEntity<UserEntity> getUser(@PathVariable("userId") Long userId, Authentication authentication) {
    validateUserAccess(userId, authentication);
    UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    return ResponseEntity.ok(user);
  }

  /**
   * 사용자를 삭제하는 DELETE API 엔드포인트
   *
   * @param userId 삭제할 사용자 ID
   * @return 상태
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    validateUserAccess(userId);
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build(); 
  }
  
  
  //JWT에서 userId를 추출하고 요청의 userId와 일치하는지 검증하는 메서드
  private void validateUserAccess(Long requestUserId, Authentication authentication) {
    Long authUserId;
    try {
      authUserId = Long.parseLong(authentication.getName());
   
    } catch (NumberFormatException e) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
      
    }

    if (!authUserId.equals(requestUserId)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
      
    }
 }
  
}