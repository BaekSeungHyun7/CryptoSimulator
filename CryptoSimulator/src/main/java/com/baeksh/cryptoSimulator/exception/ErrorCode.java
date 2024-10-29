package com.baeksh.cryptoSimulator.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_ALREADY_EXISTS("해당 유저는 이미 존재하는 유저 입니다.", 400),
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다.", 404),
    UNAUTHORIZED_ACCESS("접근 권한이 올바르지 않습니다.", 403);

    private final String message;
    private final int status;
}