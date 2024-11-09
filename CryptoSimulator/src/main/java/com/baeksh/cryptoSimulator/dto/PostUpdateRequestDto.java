package com.baeksh.cryptoSimulator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequestDto {
  private String title;
  private String content;
  private String includeSnapshot;
  
}
