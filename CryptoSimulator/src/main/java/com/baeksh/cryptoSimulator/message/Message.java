package com.baeksh.cryptoSimulator.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {
  // 성공 메시지
  INITIALIZE_PORTFOLIO_SUCCESS("초기 시드머니가 제공되었습니다."),
  TRADE_SUCCESSFUL("거래가 성공적으로 완료되었습니다.");

  private final String message;
}
