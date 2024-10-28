package com.baeksh.cryptoSimulator.dto;

import lombok.Getter;
import lombok.Setter;

public class UserDto {
    @Getter
    @Setter
    public static class SignUp { //회원가입
        private String username;  // 사용자 이름
        private String password;  // 비밀번호
        private String email;  // 이메일
        private String phone;  // 연락처
        private String role;  // 사용자 역할
    }

    @Getter
    @Setter
    public static class Update { //업데이트
        private String password;  // 비밀번호
        private String email;  // 이메일
        private String phone;  // 연락처
    }

    @Getter
    @Setter
    public static class Login { //로그인
        private String username;  // 사용자 이름
        private String password;  // 비밀번호
    }
}
