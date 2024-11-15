package com.baeksh.cryptoSimulator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Ban {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private LocalDate banEndDate;  // 차단 종료 날짜

  // 정적 팩토리 메서드 추가
  public static Ban createBan(UserEntity user, LocalDate banEndDate) {
    return Ban.builder()
      .user(user)
      .banEndDate(banEndDate)
      .build();
  }
}


