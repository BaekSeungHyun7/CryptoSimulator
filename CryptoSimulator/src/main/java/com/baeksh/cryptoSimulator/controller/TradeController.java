package com.baeksh.cryptoSimulator.controller;

import com.baeksh.cryptoSimulator.dto.TradeRequestDto;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.exception.SuccessMessage;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {
  
  private final PortfolioService portfolioService;
  private final UserRepository userRepository;
  
  //초기 시드머니 제공 API
  @PostMapping("/initialize")
  public ResponseEntity<String> initializePortfolio(Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // 잔액이 100만 원 미만일 때만 시드머니 보충
    if (user.getBalance() < 1_000_000) {
      
      // 마지막 시드머니 발급으로부터 24시간 이내라면 초기화 확인 요청
      if (user.getDebtRefTime() != null && user.getDebtRefTime().isAfter(LocalDateTime.now().minusHours(24))) {
        return ResponseEntity.status(403)
            .body("24시간 이내에 시드머니가 이미 발급되었습니다. 포트폴리오 초기화를 진행하고 다시 발급하시겠습니까? (Y/N)");
        
      }

      // 잔액을 100만 원으로 설정하고 초기화 시간 기록
      user.setBalance(1_000_000);
      user.setDebtRefTime(LocalDateTime.now());
      portfolioService.clearPortfolio(user);  // 사용자의 포트폴리오 초기화
      userRepository.save(user);
      
      return ResponseEntity.ok("초기 시드머니가 제공되어 잔액이 100만 원으로 설정되었습니다.");
      
    }
    
    return ResponseEntity.status(403).body("잔액이 충분하여 시드머니 발급이 필요하지 않습니다.");
  
  }
  
  // 매수/매도 API
  @PostMapping("/execute")
  public ResponseEntity<String> executeTrade(@RequestBody TradeRequestDto tradeRequest, Authentication authentication) {
    // JWT 토큰에서 userId 추출
    Long userId = Long.parseLong(authentication.getName());

    // userId로 사용자 조회
    UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_FOR_TRADE));

    // 거래 요청 수행
    portfolioService.updatePortfolio(user, tradeRequest.getCryptoSymbol(), tradeRequest.getAmount(), tradeRequest.getTransactionType());
    return ResponseEntity.ok(SuccessMessage.TRADE_SUCCESSFUL.getMessage());
  }
  
}
