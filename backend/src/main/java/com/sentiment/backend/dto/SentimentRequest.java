package com.sentiment.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para análise de sentimento")
public class SentimentRequest {

  @NotBlank(message = "O campo 'text' é obrigatório e não pode estar vazio.")
  @Size(min = 4, message = "O texto deve ter pelo menos 4 caracteres.")
  @Size(max = 5000, message = "O texto ultrapassou o limite máximo de 5000 caracteres.")
  @Schema(description = "Texto a ser analisado", example = "Este produto é excelente! Recomendo para todos.", requiredMode = Schema.RequiredMode.REQUIRED)
  private String text;
}
