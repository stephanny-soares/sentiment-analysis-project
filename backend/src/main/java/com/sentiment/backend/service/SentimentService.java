package com.sentiment.backend.service;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.dto.SentimentStatsResponse;
import com.sentiment.backend.mapper.SentimentAnalysisMapper;
import com.sentiment.backend.model.SentimentAnalysis;
import com.sentiment.backend.model.SentimentType;
import com.sentiment.backend.repository.SentimentAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentimentService {

  private static final String PYTHON_URL = "http://python-api:5000/predict";
  private static final double CONFIDENCE_DEFAULT = 0.85;

  private final SentimentAnalysisRepository repository;
  private final BusinessRuleService businessRuleService;
  private final SentimentAnalysisMapper mapper;
  private final RestTemplate restTemplate;

  @Transactional
  public SentimentResponse analisarSentimento(SentimentRequest request) {
    String texto = request.getText().trim();

    log.debug("Iniciando análise para texto com {} caracteres", texto.length());

    SentimentType tipoSentimento = chamarIAPython(texto);
    SentimentResponse response = construirResposta(texto, tipoSentimento);

    persistirAnalise(request, response);

    log.info("Análise concluída - Sentimento: {}, Prioridade: {}, Setor: {}",
        tipoSentimento, response.getPrioridade(), response.getSetor());

    return response;
  }

  @Transactional(readOnly = true)
  public List<SentimentAnalysis> listarHistorico() {
    return repository.findTop10ByOrderByCreatedAtDesc();
  }

  @Transactional(readOnly = true)
  public List<SentimentStatsResponse> gerarEstatisticas() {
    long total = repository.count();

    log.debug("Gerando estatísticas para {} análises", total);

    return java.util.Arrays.stream(SentimentType.values())
        .map(tipo -> {
          long quantidade = repository.countByPrediction(tipo);
          return new SentimentStatsResponse(tipo, quantidade, (double) total);
        })
        .collect(Collectors.toList());
  }

  private SentimentType chamarIAPython(String texto) {
    try {
      Map<String, String> corpo = new HashMap<>();
      corpo.put("text", texto);

      log.debug("Enviando texto para IA Python");

      Map<String, Object> resposta = restTemplate.postForObject(PYTHON_URL, corpo, Map.class);

      if (resposta != null && resposta.containsKey("previsao")) {
        String previsao = (String) resposta.get("previsao");
        log.debug("IA Python respondeu: {}", previsao);
        return mapearPrevisao(previsao);
      } else {
        log.warn("IA Python retornou resposta inválida");
      }
    } catch (Exception e) {
      log.error("Erro ao conectar com API Python: {}", e.getMessage());
    }

    log.info("Usando fallback: NEUTRO");
    return SentimentType.NEUTRO;
  }

  private SentimentType mapearPrevisao(String previsao) {
    return switch (previsao.toLowerCase()) {
      case "positivo" -> SentimentType.POSITIVO;
      case "negativo" -> SentimentType.NEGATIVO;
      default -> SentimentType.NEUTRO;
    };
  }

  private SentimentResponse construirResposta(String texto, SentimentType tipoSentimento) {
    String setor = businessRuleService.identificarSetor(texto);

    return SentimentResponse.builder()
        .previsao(tipoSentimento)
        .probabilidade(CONFIDENCE_DEFAULT)
        .prioridade(businessRuleService.identificarPrioridade(texto, tipoSentimento))
        .setor(setor)
        .tags(businessRuleService.extrairTags(texto))
        .sugestaoResposta(businessRuleService.gerarSugestao(tipoSentimento, setor))
        .build();
  }

  private void persistirAnalise(SentimentRequest request, SentimentResponse response) {
    SentimentAnalysis analysis = mapper.toEntity(request, response);
    repository.save(analysis);
    log.debug("Análise persistida com sucesso");
  }
}
