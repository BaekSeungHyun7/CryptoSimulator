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
import com.baeksh.cryptoSimulator.message.Message;
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

      if (user.getBalance() <= 1_000_000 && 
          (user.getDebtRefTime() == null || user.getDebtRefTime().isBefore(LocalDateTime.now().minusHours(24)))) {
          
          user.setBalance(1_000_000);
          user.setDebtRefTime(LocalDateTime.now());
          portfolioService.clearPortfolio(user);
          userRepository.save(user);

          return ResponseEntity.ok(Message.INITIALIZE_PORTFOLIO_SUCCESS.getMessage());
      }

      if (user.getBalance() > 1_000_000) {
          return ResponseEntity.status(403).body(Message.SEED_MONEY_NOT_ALLOWED.getMessage());
      } else {
          return ResponseEntity.status(403).body(Message.SEED_MONEY_ALREADY_ISSUED.getMessage());
      }
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
    return ResponseEntity.ok(Message.TRADE_SUCCESSFUL.getMessage());
  }
  
}
