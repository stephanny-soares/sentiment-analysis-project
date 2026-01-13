package com.sentiment.backend.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentimentAnalyzer {

  private final TextNormalizer normalizer;
  private final Tokenizer tokenizer;
  private final SentimentScoreCalculator scoreCalculator;
  private final SentimentClassifier classifier;

  public SentimentAnalysisResult analisar(String texto) {
    if (texto == null || texto.isBlank()) {
      log.warn("Texto vazio recebido para análise");
      return SentimentAnalysisResult.of(
          SentimentAnalysisResult.SentimentType.NEUTRO,
          0.0,
          0.5);
    }

    log.debug("Analisando texto com {} caracteres", texto.length());

    String textoNormalizado = normalizer.normalize(texto);
    List<String> tokens = tokenizer.tokenize(textoNormalizado);
    double score = scoreCalculator.calculate(tokens);

    SentimentAnalysisResult.SentimentType tipo = classifier.classify(score);
    double probabilidade = classifier.calculateProbability(score);

    log.debug("Análise concluída - Score: {}, Tipo: {}, Probabilidade: {}",
        String.format("%.2f", score), tipo, String.format("%.2f", probabilidade));

    return SentimentAnalysisResult.of(tipo, score, probabilidade);
  }
}
