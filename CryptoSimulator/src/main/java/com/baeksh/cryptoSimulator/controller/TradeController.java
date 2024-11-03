package com.baeksh.cryptoSimulator.controller;

import com.baeksh.cryptoSimulator.dto.TradeRequestDto;
import com.baeksh.cryptoSimulator.service.PortfolioService;
import com.baeksh.cryptoSimulator.service.TradeService;
import com.baeksh.cryptoSimulator.message.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

  private final PortfolioService portfolioService;
  private final TradeService tradeService;

  // 초기 시드머니 제공 API
  @PostMapping("/initialize")
  public ResponseEntity<String> initializePortfolio(Authentication authentication) {
    Long userId = extractUserId(authentication);
    String resultMessage = portfolioService.initializePortfolio(userId); // PortfolioService에서 호출
    return ResponseEntity.ok(resultMessage);
  }

  // 매수/매도 API
  @PostMapping("/execute")
  public ResponseEntity<String> executeTrade(@RequestBody TradeRequestDto tradeRequest, Authentication authentication) {
    Long userId = extractUserId(authentication);
    tradeService.executeTrade(userId, tradeRequest);
    return ResponseEntity.ok(Message.TRADE_SUCCESSFUL.getMessage());
  }

  // 사용자 ID 추출 메서드
  private Long extractUserId(Authentication authentication) {
    return Long.parseLong(authentication.getName());
  }
}



