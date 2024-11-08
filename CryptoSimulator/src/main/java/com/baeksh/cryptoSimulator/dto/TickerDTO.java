package com.baeksh.cryptoSimulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TickerDTO {
  
  //마켓
  private String market;                 
  
  //줄임말
  private String symbol;                  

  //한국어명
  @JsonProperty("korean_name")
  private String koreanName;
  
  //가격
  @JsonProperty("trade_price")
  private double tradePrice;              

  @JsonProperty("acc_trade_price")
  private double accTradePrice;

  // 24시간 거래량
  @JsonProperty("acc_trade_volume_24h")
  private double accTradeVolume24h;

  // 24시간 변동량
  @JsonProperty("signed_change_rate")
  private double signedChangeRate;       
    
  //포맷팅
  @JsonProperty("trade_price_str")
  private String tradePriceStr;

  @JsonProperty("acc_trade_price_str")
  private String accTradePriceStr;
    
}