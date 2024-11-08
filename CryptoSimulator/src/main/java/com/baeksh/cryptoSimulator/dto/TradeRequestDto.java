package com.baeksh.cryptoSimulator.dto;

import com.baeksh.cryptoSimulator.entity.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class TradeRequestDto {
  private String cryptoSymbol;    // 가상화폐 심볼
  private double amount;          // 거래 수량
  private TransactionType transactionType; // 거래 유형
  
  
  //테스트용
  public TradeRequestDto(String cryptoSymbol, double amount, TransactionType transactionType) {
    this.cryptoSymbol = cryptoSymbol;
    this.amount = amount;
    this.transactionType = transactionType;
  }
}
