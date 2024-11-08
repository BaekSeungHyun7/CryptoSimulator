package com.baeksh.cryptoSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import com.baeksh.cryptoSimulator.util.CustomSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {
  
  private String username;
  private BigDecimal balance;
  private BigDecimal debt;
  private LocalDateTime portfolioUpdateTime;
  private List<CryptoInfo> cryptoList;
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CryptoInfo {
    private String cryptoSymbol;
    
    @JsonSerialize(using = CustomSerializer.BigDecimalPlainStringSerializer.class)
    private BigDecimal amount;

    @JsonSerialize(using = CustomSerializer.BigDecimalPlainStringSerializer.class)
    private BigDecimal avgPrice;
    
    @JsonSerialize(using = CustomSerializer.BigDecimalPlainStringSerializer.class)
    private BigDecimal currentPrice;
    
    private String profitPercentage;
    
  }
}