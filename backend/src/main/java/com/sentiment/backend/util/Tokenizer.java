
package com.sentiment.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Tokenizer {

  private final TextNormalizer normalizer;

  public List<String> tokenize(String textoNormalizado) {
    return Arrays.stream(textoNormalizado.split("\\s+"))
        .filter(palavra -> !palavra.isEmpty())
        .map(normalizer::normalizeWord)
        .collect(Collectors.toList());
  }
}
