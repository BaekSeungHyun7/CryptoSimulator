package com.baeksh.cryptoSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommunityPostResponseDto {
  private Long postId;
  private String title;
  private String content;
  private String username;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean isNotice;
  private int recommendationCount;
  private int viewCount;
  private String portfolioSnapshot;
}
