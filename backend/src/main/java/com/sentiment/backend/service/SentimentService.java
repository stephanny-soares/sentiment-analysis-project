package com.sentiment.backend.service;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.dto.SentimentStatsResponse;
import com.sentiment.backend.model.SentimentAnalysis;
import com.sentiment.backend.model.SentimentType;
import com.sentiment.backend.repository.SentimentAnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SentimentService {

    private static final Logger logger = LoggerFactory.getLogger(SentimentService.class);

    private final SentimentAnalysisRepository repository;
    private final BusinessRuleService businessRuleService;

    // URL da API Python no Docker
    private static final String PYTHON_URL = "http://python-api:5000/predict";

    public SentimentService(SentimentAnalysisRepository repository, BusinessRuleService businessRuleService) {
        this.repository = repository;
        this.businessRuleService = businessRuleService;
    }

    /**
     * Chama a API Python para análise de sentimento.
     * Retorna "Positivo", "Negativo" ou "Neutro" como fallback.
     */
    private String chamarIAPython(String texto) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> corpo = new HashMap<>();
            corpo.put("text", texto);

            logger.info("Enviando texto para IA Python: '{}'", texto);

            Map<String, Object> resposta = restTemplate.postForObject(PYTHON_URL, corpo, Map.class);

            if (resposta != null && resposta.containsKey("previsao")) {
                String previsao = (String) resposta.get("previsao");
                logger.info("IA Python respondeu: {}", previsao);
                return previsao;
            } else {
                logger.warn("IA Python retornou resposta nula ou sem chave 'previsao'.");
            }
        } catch (Exception e) {
            logger.error("⚠️ Erro de conexão com a API Python: {}", e.getMessage());
        }
        logger.info("Usando fallback: Neutro");
        return "Neutro";
    }

    /**
     * Analisa sentimento do texto.
     */
    public SentimentResponse analisarSentimento(SentimentRequest request) {
        String texto = request.getText().trim();
        if (texto.isEmpty()) {
            logger.warn("Texto vazio enviado para análise.");
            return new SentimentResponse(SentimentType.NEUTRO, 0.0, "Baixa", new ArrayList<>(), "Geral", "Sem sugestão");
        }

        String resultadoIA = chamarIAPython(texto);

        // Converte resultado da IA para SentimentType
        SentimentType tipoModel;
        switch (resultadoIA.toLowerCase()) {
            case "positivo":
                tipoModel = SentimentType.POSITIVO;
                break;
            case "negativo":
                tipoModel = SentimentType.NEGATIVO;
                break;
            default:
                tipoModel = SentimentType.NEUTRO;
                break;
        }

        // Regras de negócio
        String setor = businessRuleService.identificarSetor(texto);
        String prioridade = businessRuleService.identificarPrioridade(texto, tipoModel);
        List<String> tags = businessRuleService.extrairTags(texto);
        String sugestao = businessRuleService.gerarSugestao(tipoModel, setor);

        // DTO de resposta
        SentimentResponse dto = new SentimentResponse(
                tipoModel,
                0.85, // Pode futuramente vir da IA
                prioridade,
                tags,
                setor,
                sugestao
        );

        // Salva no banco
        SentimentAnalysis analysis = new SentimentAnalysis(request, dto);
        repository.save(analysis);

        logger.info("Análise finalizada: '{}' -> {} | Setor: {} | Prioridade: {}", texto, tipoModel, setor, prioridade);

        return dto;
    }

    public List<SentimentAnalysis> listarHistorico() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<SentimentStatsResponse> gerarEstatisticas() {
        long total = repository.count();
        List<SentimentStatsResponse> stats = new ArrayList<>();
        for (SentimentType tipo : SentimentType.values()) {
            long quantidade = repository.countByPrediction(tipo);
            stats.add(new SentimentStatsResponse(tipo, quantidade, (double) total));
        }
        return stats;
    }
}