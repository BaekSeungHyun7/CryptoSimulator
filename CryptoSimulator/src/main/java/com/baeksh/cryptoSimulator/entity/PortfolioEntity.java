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
@Table(name = "portfolio")
public class PortfolioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long portfolioId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private String cryptoSymbol;
  
  private double amount;
  
  private double avgPrice;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  // 기본 포트폴리오 생성 메서드
  public static PortfolioEntity defaultPortfolio(UserEntity user, String cryptoSymbol) {
    return PortfolioEntity.builder()
        .user(user)
        .cryptoSymbol(cryptoSymbol)
        .amount(0)
        .avgPrice(0)
        .build();
  }
}

