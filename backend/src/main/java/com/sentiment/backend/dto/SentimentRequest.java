package com.sentiment.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
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

  @Min(value = 1, message = "Rating deve ser entre 1 e 5")
  @Max(value = 5, message = "Rating deve ser entre 1 e 5")
  @Schema(description = "Avaliação em estrelas (1-5) - Opcional para modelo enhanced", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer rating;

  @Schema(description = "Se recomendaria a um amigo - Opcional para modelo enhanced", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Boolean recommendToFriend;
}
