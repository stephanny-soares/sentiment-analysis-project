package com.sentiment.backend.controller;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.dto.SentimentStatsResponse;
import com.sentiment.backend.service.SentimentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller responsável pelos endpoints de análise de sentimento e estatísticas.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/sentiment")
public class SentimentController {

    private static final Logger logger = LoggerFactory.getLogger(SentimentController.class);
    private final SentimentService sentimentService;

    @Autowired
    public SentimentController(SentimentService sentimentService) {
        this.sentimentService = sentimentService;
    }

    /**
     * Endpoint POST que recebe um texto e retorna a análise de sentimento.
     */
    @PostMapping
    public ResponseEntity<SentimentResponse> analisar(@Valid @RequestBody SentimentRequest request) {
        logger.info("Recebido texto para análise: {}", request.getText());
        SentimentResponse response = sentimentService.analisarSentimento(request);
        logger.info("Retornando previsão: {} ({})", response.getPrevisao(), response.getProbabilidade());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint GET que retorna estatísticas de sentimento.
     */
    @GetMapping("/stats")
    public ResponseEntity<List<SentimentStatsResponse>> getStats() {
        List<SentimentStatsResponse> stats = sentimentService.gerarEstatisticas();
        return ResponseEntity.ok(stats);
    }
}
