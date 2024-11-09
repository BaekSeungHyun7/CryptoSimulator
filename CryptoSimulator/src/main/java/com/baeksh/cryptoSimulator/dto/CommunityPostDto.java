package com.baeksh.cryptoSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostDto {
  private String title;
  private String content;
  private String includeSnapshot;
  private PortfolioDto portfolioSnapshot;
}
