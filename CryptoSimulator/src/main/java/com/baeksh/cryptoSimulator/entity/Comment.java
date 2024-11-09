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
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;  // 댓글 ID

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private CommunityPost post;  // 게시물 ID

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;  // 작성자 ID

  @Column(columnDefinition = "TEXT")
  private String comment;  // 댓글 내용

  private LocalDateTime commentedAt;  // 작성일

  @PrePersist
  protected void onCreate() {
    this.commentedAt = LocalDateTime.now();
  }
}
