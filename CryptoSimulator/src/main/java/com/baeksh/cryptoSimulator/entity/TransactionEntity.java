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
@Table(name = "transaction")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String cryptoSymbol; // 거래한 가상화폐 심볼
    
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // 매수와 매도
    
    private double amount; // 거래 수량
    private double price; // 거래 당시 가격

    @Column(nullable = false)
    private LocalDateTime transactionTime;

    @PrePersist
    protected void onCreate() {
      transactionTime = LocalDateTime.now();
    }
}
