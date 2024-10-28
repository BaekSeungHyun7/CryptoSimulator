package com.baeksh.cryptoSimulator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 20)
    private String username;  // 사용자 이름 (고유값)

    @Column(nullable = false)
    private String password;  // 사용자 비밀번호 (암호화 저장)

    @Column(nullable = false, length = 50)
    private String email;  // 사용자 이메일

    @Column(length = 20)
    private String phone;  // 사용자 연락처

    @Column(nullable = false)
    private String role;  // 사용자 역할 //ADMIN USER MODERATOR

    @Column(nullable = false)
    private double balance = 1000000;  // 초기 잔액 100만 원 설정

    @Column(nullable = false)
    private double debt = 0;  // 초기 빚 0원 설정
    
    /*
    @Column(nullable = false)
    private boolean emailVerified = false;  // 이메일 인증 여부
    //다른 테이블을 추가해야할 것 같은 상태
    */
}
