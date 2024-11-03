package com.baeksh.cryptoSimulator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeRequestDto {
  private String cryptoSymbol;    // 가상화폐 심볼
  private double amount;          // 거래 수량
  private String transactionType; // 거래 유형 (BUY 와 SELL)
}