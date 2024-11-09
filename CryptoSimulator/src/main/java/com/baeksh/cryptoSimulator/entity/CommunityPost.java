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
public class CommunityPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;  // 게시물 ID

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;  // 작성자 ID

  @Column(nullable = false, length = 255)
  private String title;  // 게시물 제목

  @Column(columnDefinition = "TEXT")
  private String content;  // 게시물 내용

  @Column(columnDefinition = "TEXT")
  private String portfolioSnapshot;  // 포트폴리오 스냅샷 (JSON 형식)

  private LocalDateTime createdAt;  // 작성일
  private LocalDateTime updatedAt;  // 수정일

  private boolean isNotice = false;  // 공지 여부
  private int recommendationCount = 0;  // 추천 수

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
  
  
  @Column(nullable = false)
  private int viewCount = 0;  // 조회수, 기본값 0
  
  //조회수 증가
  public void incrementViewCount() {
    this.viewCount++;
  }
}
