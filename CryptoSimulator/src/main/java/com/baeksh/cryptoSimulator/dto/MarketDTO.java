package com.baeksh.cryptoSimulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDTO {
    private String market;

    @JsonProperty("korean_name")
    private String koreanName;
}

