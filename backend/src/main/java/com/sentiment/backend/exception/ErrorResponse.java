package com.sentiment.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padronizada de erro")
public class ErrorResponse {

  @Schema(description = "Timestamp do erro", example = "2025-01-03T14:30:00")
  private LocalDateTime timestamp;

  @Schema(description = "Código HTTP", example = "400")
  private Integer status;

  @Schema(description = "Tipo do erro", example = "Validation Failed")
  private String error;

  @Schema(description = "Mensagem descritiva", example = "Erro na validação")
  private String message;

  @Schema(description = "Caminho da requisição", example = "/sentiment")
  private String path;

  @Schema(description = "Erros de validação detalhados")
  private Map<String, String> validationErrors;
}
