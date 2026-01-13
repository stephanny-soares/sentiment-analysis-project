package com.sentiment.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sentiment.backend.model.SentimentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta da análise de sentimento")
public class SentimentResponse {

  @JsonProperty("prediction")
  @Schema(description = "Sentimento previsto", example = "POSITIVO")
  private SentimentType previsao;

  @JsonProperty("confidence")
  @Schema(description = "Confiança da previsão (0.0 a 1.0)", example = "0.95")
  private Double probabilidade;

  @Schema(description = "Prioridade de atendimento", example = "ALTA")
  private String prioridade;

  @Schema(description = "Tags relacionadas ao sentimento", example = "[\"elogio\", \"produto\"]")
  private List<String> tags;

  @Schema(description = "Setor recomendado", example = "Atendimento")
  private String setor;

  @Schema(description = "Sugestão de resposta automática")
  private String sugestaoResposta;
}
