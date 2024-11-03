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

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {
  
  private final PortfolioService portfolioService;
  private final UserRepository userRepository;
  
  //초기 시드머니 제공 API
  @PostMapping("/initialize")
  public ResponseEntity<String> initializePortfolio(Authentication authentication) {
    // JWT 토큰에서 userId 추출
    Long userId = Long.parseLong(authentication.getName());

    // userId로 사용자 조회
    UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_FOR_TRADE));

    portfolioService.initializePortfolio(user);
    return ResponseEntity.ok(SuccessMessage.INITIALIZE_PORTFOLIO_SUCCESS.getMessage());
    
    /* TO-DO
     * 초기 시드머니 발급 제한하는 조건 (빚 상태, 시간제한)
     * 옵션 투자 기능 제작하게 되면 구현
     * */
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
