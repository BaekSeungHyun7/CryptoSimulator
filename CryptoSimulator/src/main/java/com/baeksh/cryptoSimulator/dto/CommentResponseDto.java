package com.baeksh.cryptoSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponseDto {
  private Long commentId;
  private String comment;
  private String username;
  private LocalDateTime commentedAt;
}
