package com.baeksh.cryptoSimulator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recommendationId;  // 추천 ID

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private CommunityPost post;  // 게시물 ID

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;  // 추천한 사용자 ID
}
