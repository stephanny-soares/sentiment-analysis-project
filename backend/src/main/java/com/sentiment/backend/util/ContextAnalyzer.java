package com.sentiment.backend.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContextAnalyzer {

  public boolean hasNegation(List<String> tokens, int index) {
    return (index > 0 && SentimentLexicon.NEGACOES.contains(tokens.get(index - 1))) ||
        (index > 1 && SentimentLexicon.NEGACOES.contains(tokens.get(index - 2)));
  }

  public boolean hasIntensifier(List<String> tokens, int index) {
    return index > 0 && SentimentLexicon.INTENSIFICADORES.contains(tokens.get(index - 1));
  }

  public boolean isAdversative(String token) {
    return SentimentLexicon.ADVERSATIVAS.contains(token);
  }
}
