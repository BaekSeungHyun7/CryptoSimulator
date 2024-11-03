package com.baeksh.cryptoSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import com.baeksh.cryptoSimulator.util.CustomSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {
  private String cryptoSymbol;
  
  @JsonSerialize(using = CustomSerializer.BigDecimalPlainStringSerializer.class)
  private BigDecimal amount;

  @JsonSerialize(using = CustomSerializer.BigDecimalPlainStringSerializer.class)
  private BigDecimal avgPrice;
}