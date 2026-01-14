package com.sentiment.backend.controller;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.dto.SentimentStatsResponse;
import com.sentiment.backend.service.SentimentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sentiment")
@RequiredArgsConstructor
@Tag(name = "Sentiment Analysis", description = "APIs para análise de sentimento de textos e estatísticas")
public class SentimentController {

  private final SentimentService sentimentService;

  @Operation(summary = "Analisa o sentimento de um texto", description = "Recebe um texto e retorna a classificação do sentimento com probabilidade")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Análise realizada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SentimentResponse.class))),
      @ApiResponse(responseCode = "400", description = "Dados inválidos"),
      @ApiResponse(responseCode = "500", description = "Erro interno")
  })
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SentimentResponse> analisar(
      @Parameter(description = "Texto para análise", required = true) @Valid @RequestBody SentimentRequest request) {

    log.debug("Recebida requisição de análise com {} caracteres", request.getText().length());

    SentimentResponse response = sentimentService.analisarSentimento(request);

    log.info("Análise concluída - Sentimento: {}, Confiança: {}%",
        response.getPrevisao(),
        String.format("%.2f", response.getProbabilidade() * 100));

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Obtém estatísticas", description = "Retorna métricas agregadas das análises realizadas")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Estatísticas recuperadas", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SentimentStatsResponse.class)))),
      @ApiResponse(responseCode = "500", description = "Erro ao buscar estatísticas")
  })
  @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SentimentStatsResponse>> obterEstatisticas() {
    log.debug("Requisição de estatísticas recebida");

    List<SentimentStatsResponse> stats = sentimentService.gerarEstatisticas();

    log.info("Estatísticas geradas: {} tipos de sentimento", stats.size());

    return ResponseEntity.ok(stats);
  }

  @DeleteMapping("/clear")
  public ResponseEntity<Void> limpar() {
    sentimentService.limparHistorico();
    return ResponseEntity.ok().build();
  }
}
