package com.baeksh.cryptoSimulator.controller;


import com.baeksh.cryptoSimulator.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.baeksh.cryptoSimulator.dto.PortfolioDto;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
  
  private final PortfolioService portfolioService;

  //사용자 포트폴리오 조회
  @GetMapping
  public ResponseEntity<PortfolioDto> getUserPortfolio(Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    PortfolioDto portfolio = portfolioService.getUserPortfolio(userId);
    return ResponseEntity.ok(portfolio);
  }
}