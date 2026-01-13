package com.sentiment.backend.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentimentScoreCalculator {

  private static final double PESO_ADVERSATIVA = 0.3;
  private static final double PESO_POS_ADVERSATIVA = 1.5;
  private static final double PESO_INTENSIFICADOR = 2.0;

  private final ContextAnalyzer contextAnalyzer;
  private final PolarityChecker polarityChecker;

  public double calculate(List<String> tokens) {
    double scoreTotal = 0.0;
    boolean efeitoAdversativa = false;

    for (int i = 0; i < tokens.size(); i++) {
      String token = tokens.get(i);

      if (contextAnalyzer.isAdversative(token)) {
        scoreTotal *= PESO_ADVERSATIVA;
        efeitoAdversativa = true;
        continue;
      }

      boolean temNegacao = contextAnalyzer.hasNegation(tokens, i);
      boolean temIntensificador = contextAnalyzer.hasIntensifier(tokens, i);

      double peso = calculateWeight(efeitoAdversativa, temIntensificador);

      boolean ehPositivo = polarityChecker.isPositive(token);
      boolean ehNegativo = polarityChecker.isNegative(token);

      if (ehPositivo) {
        scoreTotal += temNegacao ? -peso : peso;
      } else if (ehNegativo) {
        scoreTotal += temNegacao ? peso : -peso;
      }
    }

    return scoreTotal;
  }

  private double calculateWeight(boolean efeitoAdversativa, boolean temIntensificador) {
    double pesoImportancia = efeitoAdversativa ? PESO_POS_ADVERSATIVA : 1.0;
    double pesoIntensidade = temIntensificador ? PESO_INTENSIFICADOR : 1.0;
    return pesoImportancia * pesoIntensidade;
  }
}
