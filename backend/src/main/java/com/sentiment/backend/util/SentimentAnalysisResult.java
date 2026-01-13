package com.sentiment.backend.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SentimentAnalysisResult {

  public enum SentimentType {
    POSITIVO,
    NEUTRO,
    NEGATIVO
  }

  private final SentimentType type;
  private final double score;
  private final double probability;

  public static SentimentAnalysisResult of(SentimentType type, double score, double probability) {
    double normalizedProbability = Math.max(0.0, Math.min(1.0, probability));
    return new SentimentAnalysisResult(type, score, normalizedProbability);
  }

  public double getProbabilidade() {
    return probability;
  }

  public boolean hasHighConfidence() {
    return probability >= 0.7;
  }

  public double getConfidencePercentage() {
    return probability * 100;
  }
}
