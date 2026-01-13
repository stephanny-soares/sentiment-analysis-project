package com.sentiment.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Slf4j
@Component
public class TextNormalizer {

  public String normalize(String texto) {
    if (texto == null || texto.isBlank()) {
      return "";
    }

    return Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .replaceAll("[^a-z\\s]", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  public String normalizeWord(String palavra) {
    if (palavra == null || palavra.isEmpty()) {
      return "";
    }

    String normalizada = Normalizer.normalize(palavra.toLowerCase(), Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .replaceAll("[^a-z]", "");

    if (normalizada.length() <= 3) {
      return normalizada;
    }

    if (normalizada.endsWith("s")) {
      normalizada = normalizada.substring(0, normalizada.length() - 1);
    }

    if (normalizada.length() > 3 &&
        (normalizada.endsWith("a") || normalizada.endsWith("o") || normalizada.endsWith("e"))) {
      normalizada = normalizada.substring(0, normalizada.length() - 1);
    }

    return normalizada;
  }
}
