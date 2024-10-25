package com.baeksh.cryptoSimulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TickerDTO {
    private String market;                  // 마켓
    private String symbol;                  // 줄임말

    @JsonProperty("korean_name")
    private String koreanName;              // 한국어명

    @JsonProperty("trade_price")
    private double tradePrice;              // 가격

    @JsonProperty("acc_trade_price")
    private double accTradePrice;           // 시가총액

    @JsonProperty("acc_trade_volume_24h")
    private double accTradeVolume24h;       // 24시간 거래량

    @JsonProperty("signed_change_rate")
    private double signedChangeRate;        // 24시간 변동량
}