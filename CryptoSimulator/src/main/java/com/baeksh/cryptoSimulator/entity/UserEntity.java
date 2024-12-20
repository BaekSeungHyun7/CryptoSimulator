package com.baeksh.cryptoSimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    
    @Column(unique = true, nullable = false, length = 50)
    private String email;  // 사용자 이메일

    @Column(nullable = false)
    private String password;  // 사용자 비밀번호 (암호화 저장)

    @Column(length = 20)
    private String phone;  // 사용자 연락처

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // 사용자 역할 (ADMIN, USER, MODERATOR)

    @Column(nullable = false)
    private double balance = 1000000;  // 초기 잔액 100만 원 설정

    @Column(nullable = false)
    private double debt = 0;  // 초기 빚 0원 설정
    
    @Column(name = "debt_ref_time")
    private LocalDateTime debtRefTime; // 시드머니 발급 시간
    
    public boolean isAdmin() {
      return this.role == Role.ADMIN; //어드민
    }
    
    public boolean isModeratorOrAdmin() {
      return this.role == Role.ADMIN || this.role == Role.MODERATOR; //게시판 권한자
    }
}
