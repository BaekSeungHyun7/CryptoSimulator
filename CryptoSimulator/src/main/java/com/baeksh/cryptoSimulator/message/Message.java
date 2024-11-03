package com.baeksh.cryptoSimulator.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {
    // 성공 메시지
    INITIALIZE_PORTFOLIO_SUCCESS("초기 시드머니가 제공되었습니다.", MessageType.SUCCESS),
    TRADE_SUCCESSFUL("거래가 성공적으로 완료되었습니다.", MessageType.SUCCESS),
    
    // 상태 메시지
    SEED_MONEY_ALREADY_ISSUED("24시간 이내에 시드머니가 이미 발급되었습니다. 포트폴리오 초기화를 진행하고 다시 발급하시겠습니까? (빚은 초기화되지 않습니다.) (Y/N)", MessageType.STATUS),
    INSUFFICIENT_FUNDS("잔액이 부족하여 거래를 진행할 수 없습니다.", MessageType.STATUS),
    SEED_MONEY_NOT_ALLOWED("잔액이 충분하여 시드머니 발급이 필요하지 않습니다.", MessageType.STATUS);

    private final String message;
    private final MessageType type; // 메시지 유형 (성공, 상태)
}
