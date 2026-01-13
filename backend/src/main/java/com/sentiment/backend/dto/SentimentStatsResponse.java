package com.sentiment.backend.dto;

import com.sentiment.backend.model.SentimentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Schema(description = "Estatísticas agregadas de sentimento")
public class SentimentStatsResponse {

  @Schema(description = "Tipo de sentimento", example = "POSITIVO")
  private final SentimentType sentimento;

  @Schema(description = "Quantidade de análises deste sentimento", example = "150")
  private final long quantidade;

  @Schema(description = "Percentual do total", example = "75.5")
  private final double percentual;

  public SentimentStatsResponse(SentimentType sentimento, long quantidade, double total) {
    this.sentimento = sentimento;
    this.quantidade = quantidade;
    this.percentual = calcularPercentual(quantidade, total);
  }

  private double calcularPercentual(long qtd, double total) {
    if (total == 0) {
      return 0.0;
    }

    double valor = (qtd * 100.0) / total;

    return BigDecimal.valueOf(valor)
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
  }
}
