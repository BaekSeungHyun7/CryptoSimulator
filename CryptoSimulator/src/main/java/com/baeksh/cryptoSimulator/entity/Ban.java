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
public class Ban {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private LocalDateTime banEndDate;  // 차단 종료 날짜

  public Ban(UserEntity user, LocalDateTime banEndDate) {
    this.user = user;
    this.banEndDate = banEndDate;
  }
}

