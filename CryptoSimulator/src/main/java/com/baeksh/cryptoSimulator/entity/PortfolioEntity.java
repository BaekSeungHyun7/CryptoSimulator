package com.baeksh.cryptoSimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

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
  
  //가상화폐 심볼
  private String cryptoSymbol;
  
  //보유 수량
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00000000")
  private double amount;
  
  //평균 매수가
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
  private double avgPrice;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  //엔티티 처음 저장/불러올때 실행되는 매서드, 필드를 현재 시간으로 갱신
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
