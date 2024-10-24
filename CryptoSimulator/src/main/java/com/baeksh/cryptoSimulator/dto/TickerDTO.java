package com.baeksh.cryptoSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TickerDTO {
    private String market;      //마켓
    private String symbol;       // 줄임말
    private String koreanName;   // 한국어 이름
    private double trade_price;  //가격
    private double acc_trade_price; //시가총액
    private double acc_trade_volume_24h; //24시간 거래량
    private double signed_change_rate;  //24시간 변동량
}
