package com.baeksh.cryptoSimulator.dto;

public enum IncludeSnapshotOption {
  YES("Y"),
  NO("N");

  private final String value;

  IncludeSnapshotOption(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static boolean isYes(String option) {
    return YES.value.equalsIgnoreCase(option);
  }
}