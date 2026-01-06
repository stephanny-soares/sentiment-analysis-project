package com.sentiment.backend.service;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.dto.SentimentStatsResponse;
import com.sentiment.backend.model.SentimentAnalysis;
import com.sentiment.backend.model.SentimentType;
import com.sentiment.backend.repository.SentimentAnalysisRepository;
import com.sentiment.backend.util.SentimentAnalyzer;
import com.sentiment.backend.util.SentimentAnalysisResult;
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

    // URL do seu servidor Python que está rodando no terminal
    private final String PYTHON_URL = "http://192.168.0.17:5000/predict/sentiment";
    // final String PYTHON_URL = "http://127.0.0.1:5000/predict/sentiment";

    public SentimentService(SentimentAnalysisRepository repository, BusinessRuleService businessRuleService) {
        this.repository = repository;
        this.businessRuleService = businessRuleService;
    }

    /**
     * Método auxiliar para conversar com a IA em Python
     */
    private int chamarIAPython(String texto) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> corpo = new HashMap<>();
            corpo.put("texto", texto);

            // Faz a chamada para o Python e recebe um Map (JSON)
            Map<String, Object> resposta = restTemplate.postForObject(PYTHON_URL, corpo, Map.class);

            if (resposta != null && resposta.containsKey("sentimento")) {
                return (Integer) resposta.get("sentimento");
            }
        } catch (Exception e) {
            logger.error("⚠️ Não foi possível conectar com a IA Python. Verifique se o terminal está aberto! Erro: {}", e.getMessage());
        }
        return 3; // Retorna 3 (Neutro) como fallback caso a IA esteja desligada
    }

    public SentimentResponse analisarSentimento(SentimentRequest request) {
        String texto = request.getText();

        logger.info("Enviando texto para IA Python...");
        int notaIA = chamarIAPython(texto);
        logger.info("IA respondeu com nota: {}", notaIA);

        SentimentType tipoModel;

        if (notaIA == 0) {
            tipoModel = SentimentType.NEGATIVO; // 0 = Ruim no novo modelo
        } else if (notaIA == 2) {
            tipoModel = SentimentType.POSITIVO; // 2 = Bom no novo modelo
        } else {
            tipoModel = SentimentType.NEUTRO;   // 1 (ou erros) = Neutro
        }

        // 2️⃣ ENRIQUECIMENTO COM REGRAS DE NEGÓCIO
        String prioridade = businessRuleService.identificarPrioridade(texto, tipoModel);
        String setor = businessRuleService.identificarSetor(texto);
        List<String> tags = businessRuleService.extrairTags(texto);
        String sugestao = businessRuleService.gerarSugestao(tipoModel, setor);

        // 3️⃣ Cria DTO de resposta
        SentimentResponse dto = new SentimentResponse(
                tipoModel,
                0.85, // Probabilidade fixa
                prioridade,
                tags,
                setor,
                sugestao
        );

        // 4️⃣ Salva no banco
        SentimentAnalysis analysis = new SentimentAnalysis(request, dto);
        repository.save(analysis);

        logger.info("Análise Finalizada: '{}' -> {} | Prioridade: {}", texto, tipoModel, prioridade);

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