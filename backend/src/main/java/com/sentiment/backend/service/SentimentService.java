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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class SentimentService {

    private static final Logger logger = LoggerFactory.getLogger(SentimentService.class);
    private final SentimentAnalysisRepository repository;
    private final BusinessRuleService businessRuleService; // <--- NOVA INJEÇÃO

    public SentimentService(SentimentAnalysisRepository repository, BusinessRuleService businessRuleService) {
        this.repository = repository;
        this.businessRuleService = businessRuleService;
    }

    public SentimentResponse analisarSentimento(SentimentRequest request) {
        // 1️⃣ Analisa usando a IA (SentimentAnalyzer interno)
        SentimentAnalysisResult resultado = SentimentAnalyzer.analisar(request.getText());

        SentimentType tipoModel = switch (resultado.getType()) {
            case POSITIVO -> SentimentType.POSITIVO;
            case NEGATIVO -> SentimentType.NEGATIVO;
            default -> SentimentType.NEUTRO;
        };

        // 2️⃣ ENRIQUECIMENTO COM REGRAS DE NEGÓCIO (BACK-END)
        String texto = request.getText();
        String prioridade = businessRuleService.identificarPrioridade(texto, tipoModel);
        String setor = businessRuleService.identificarSetor(texto);
        List<String> tags = businessRuleService.extrairTags(texto);
        String sugestao = businessRuleService.gerarSugestao(tipoModel, setor);

        // 3️⃣ Cria DTO de resposta com TODOS os dados
        SentimentResponse dto = new SentimentResponse(
                tipoModel,
                resultado.getProbabilidade(),
                prioridade,
                tags,
                setor,
                sugestao
        );

        // 4️⃣ Salva no banco (Agora a Entity já sabe como lidar com o DTO novo)
        SentimentAnalysis analysis = new SentimentAnalysis(request, dto);
        repository.save(analysis);

        logger.info("Análise Completa: '{}' -> {} | Prioridade: {} | Setor: {}",
                texto, tipoModel, prioridade, setor);

        return dto;
    }

    /**
     * Retorna as últimas 10 análises salvas no banco.
     */
    public List<SentimentAnalysis> listarHistorico() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }

    /**
     * Retorna estatísticas de sentimento com quantidade e percentual.
     */
    public List<SentimentStatsResponse> gerarEstatisticas() {
        long total = repository.count();

        List<SentimentStatsResponse> stats = new ArrayList<>();
        for (SentimentType tipo : SentimentType.values()) {
            // AQUI ESTAVA O ERRO: Mudamos para countByPrediction para bater com o Repository
            long quantidade = repository.countByPrediction(tipo);
            stats.add(new SentimentStatsResponse(tipo, quantidade, (double) total));
        }

        return stats;
    }
}