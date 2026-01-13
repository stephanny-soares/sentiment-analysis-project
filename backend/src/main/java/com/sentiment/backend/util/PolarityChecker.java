package com.sentiment.backend.util;

import org.springframework.stereotype.Component;

@Component
public class PolarityChecker {

  public boolean isPositive(String palavra) {
    return checkPolarity(palavra, SentimentLexicon.POSITIVAS);
  }

  public boolean isNegative(String palavra) {
    return checkPolarity(palavra, SentimentLexicon.NEGATIVAS);
  }

  private boolean checkPolarity(String palavra, java.util.List<String> lexicon) {
    return lexicon.stream().anyMatch(palavra::startsWith);
  }
}
