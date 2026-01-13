package com.sentiment.backend.util;

import org.springframework.stereotype.Component;

@Component
public class SentimentClassifier {

  private static final double THRESHOLD_POSITIVO = 0.2;
  private static final double THRESHOLD_NEGATIVO = -0.2;
  private static final double PROBABILIDADE_BASE = 0.5;
  private static final double PROBABILIDADE_INCREMENTO = 0.1;
  private static final int MAX_SCORE_PARA_PROBABILIDADE = 5;

  public SentimentAnalysisResult.SentimentType classify(double score) {
    if (score > THRESHOLD_POSITIVO) {
      return SentimentAnalysisResult.SentimentType.POSITIVO;
    } else if (score < THRESHOLD_NEGATIVO) {
      return SentimentAnalysisResult.SentimentType.NEGATIVO;
    } else {
      return SentimentAnalysisResult.SentimentType.NEUTRO;
    }
  }

  public double calculateProbability(double score) {
    double scoreAbs = Math.abs(score);
    double incremento = PROBABILIDADE_INCREMENTO * Math.min(scoreAbs, MAX_SCORE_PARA_PROBABILIDADE);
    return Math.min(1.0, PROBABILIDADE_BASE + incremento);
  }
}
