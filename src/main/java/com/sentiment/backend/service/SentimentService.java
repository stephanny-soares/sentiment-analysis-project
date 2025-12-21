package com.sentiment.backend.service;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.model.SentimentAnalysis;
import com.sentiment.backend.model.SentimentType;
import com.sentiment.backend.repository.SentimentAnalysisRepository;
import com.sentiment.backend.util.SentimentAnalyzer;
import com.sentiment.backend.util.SentimentAnalysisResult;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class SentimentService {

    private static final Logger logger = LoggerFactory.getLogger(SentimentService.class);
    private final SentimentAnalysisRepository repository;

    public SentimentService(SentimentAnalysisRepository repository) {
        this.repository = repository;
    }

    /**
     * Analisa o texto recebido, salva no banco e retorna o DTO de resposta.
     */
    public SentimentResponse analisarSentimento(SentimentRequest request) {

        // 1️⃣ Analisa usando SentimentAnalyzer (interno)
        SentimentAnalysisResult resultado = SentimentAnalyzer.analisar(request.getText());

        // 2️⃣ Converte enum do util para enum do model
        SentimentType tipoModel;
        switch (resultado.getType()) {
            case POSITIVO -> tipoModel = SentimentType.POSITIVO;
            case NEGATIVO -> tipoModel = SentimentType.NEGATIVO;
            case NEUTRO -> tipoModel = SentimentType.NEUTRO;
            default -> tipoModel = SentimentType.NEUTRO;
        }

        // 3️⃣ Cria DTO de resposta (para o Controller)
        SentimentResponse dto = new SentimentResponse(tipoModel, resultado.getProbability());

        // 4️⃣ Cria entidade e salva no banco
        SentimentAnalysis analysis = new SentimentAnalysis(request, dto);
        repository.save(analysis);

        // 5️⃣ Log para monitoramento
        logger.info("Análise salva: '{}' → {} (score: {}, prob: {})",
                request.getText(),
                dto.getPrevisao(),
                resultado.getScore(),
                dto.getProbabilidade()
        );

        return dto;
    }

    /**
     * Retorna as últimas 10 análises salvas no banco.
     */
    public List<SentimentAnalysis> listarHistorico() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }
}
