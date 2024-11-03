package com.baeksh.cryptoSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequestDto {
  private String cryptoSymbol;    // 가상화폐 심볼
  private double amount;          // 거래 수량
  private String transactionType; // 거래 유형 (BUY와 SELL)
}
