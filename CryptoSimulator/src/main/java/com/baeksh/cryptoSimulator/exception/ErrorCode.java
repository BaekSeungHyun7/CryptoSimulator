package com.baeksh.cryptoSimulator.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  USER_ALREADY_EXISTS("해당 유저는 이미 존재하는 유저 입니다.", 400),
  USER_NOT_FOUND("해당 유저를 찾을 수 없습니다.", 404),
  UNAUTHORIZED_ACCESS("접근 권한이 올바르지 않습니다.", 403),
  INSUFFICIENT_FUNDS("잔액이 부족합니다. 거래할 수 없습니다.", 400),
  INSUFFICIENT_HOLDINGS("보유량이 부족하여 매도할 수 없습니다.", 400),
  USER_NOT_FOUND_FOR_TRADE("거래 사용자를 찾을 수 없습니다.", 404),
  USER_NOT_FOUND_FOR_PORTFOLIO("사용자의 포트폴리오를 찾을 수 없습니다.", 404),
  SEED_MONEY_ALREADY_ISSUED("24시간 이내에 시드머니가 이미 발급되었습니다.", 403),
  SEED_MONEY_NOT_ALLOWED("잔액이 충분하여 시드머니 발급이 필요하지 않습니다.", 403);

  private final String message;
  private final int status;
}
