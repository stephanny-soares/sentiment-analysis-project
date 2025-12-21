package com.sentiment.backend.controller;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.service.SentimentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsável pelo endpoint de análise de sentimento.
 * Recebe requisições HTTP, chama o Service e devolve a resposta.
 */
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
     *
     * @param request DTO contendo o texto
     * @return ResponseEntity com o DTO de resposta
     */
    @PostMapping
    public ResponseEntity<SentimentResponse> analisar(@Valid @RequestBody SentimentRequest request) {
        logger.info("Recebido texto para análise: {}", request.getText());
        SentimentResponse response = sentimentService.analisarSentimento(request);
        logger.info("Retornando previsão: {} ({})", response.getPrevisao(), response.getProbabilidade());
        return ResponseEntity.ok(response);
    }
}
