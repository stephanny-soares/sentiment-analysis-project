package com.sentiment.backend.service;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.dto.SentimentStatsResponse;
import com.sentiment.backend.model.SentimentAnalysis;
import com.sentiment.backend.model.SentimentType;
import com.sentiment.backend.repository.SentimentAnalysisRepository;
import com.sentiment.backend.mapper.SentimentAnalysisMapper;
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

  private static final String PYTHON_URL = "http://python-api:8000/predict";
  private static final String PYTHON_URL_AUTO = "http://python-api:8000/predict/auto";
  private static final double CONFIDENCE_DEFAULT = 0.85;

  private final SentimentAnalysisRepository repository;
  private final BusinessRuleService businessRuleService;
  private final SentimentAnalysisMapper mapper;
  private final RestTemplate restTemplate;

  @Transactional
  public SentimentResponse analisarSentimento(SentimentRequest request) {
    String texto = request.getText().trim();
    log.debug("Iniciando análise para texto com {} caracteres", texto.length());
  
    Map<String, Object> dadosIA = chamarIAPython(texto, request.getRating(), request.getRecommendToFriend());

    if (dadosIA == null) {
      dadosIA = new HashMap<>();
      log.warn("Resposta da IA nula. Usando valores padrão.");
    }

    String previsaoStr = (String) dadosIA.getOrDefault("previsao", "neutro");
    SentimentType tipoSentimento = mapearPrevisao(previsaoStr);
    SentimentResponse response = construirResposta(texto, tipoSentimento, dadosIA);

    persistirAnalise(request, response);

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

  @Transactional
  public void limparHistorico() {
    repository.deleteAll();
  }

  private Map<String, Object> chamarIAPython(String texto, Integer rating, Boolean recommendToFriend) {
    try {
      Map<String, Object> corpo = new HashMap<>();
      corpo.put("text", texto);

      boolean useEnhanced = rating != null && recommendToFriend != null;
      String url = useEnhanced ? PYTHON_URL_AUTO : PYTHON_URL;

      return restTemplate.postForObject(url, corpo, Map.class);
    } catch (Exception e) {
      log.error("Erro ao conectar com API Python: {}", e.getMessage());
      return null;
    }
  }

  private SentimentResponse construirResposta(String texto, SentimentType tipoSentimento, Map<String, Object> dadosIA) {
    String setor = businessRuleService.identificarSetor(texto);
    Double confianca = CONFIDENCE_DEFAULT;

    if (dadosIA != null && dadosIA.containsKey("probabilidade")) {
      Object prob = dadosIA.get("probabilidade");
      if (prob instanceof Number n) {
        confianca = n.doubleValue();
      }
    }

    return SentimentResponse.builder()
            .previsao(tipoSentimento)
            .probabilidade(confianca)
            .prioridade(businessRuleService.identificarPrioridade(texto, tipoSentimento))
            .setor(setor)
            .tags(businessRuleService.extrairTags(texto))
            .sugestaoResposta(businessRuleService.gerarSugestao(tipoSentimento, setor))
            .build();
  }

  private SentimentType mapearPrevisao(String previsao) {
    if (previsao == null) return SentimentType.NEUTRO;
    return switch (previsao.toLowerCase()) {
      case "positivo" -> SentimentType.POSITIVO;
      case "negativo" -> SentimentType.NEGATIVO;
      default -> SentimentType.NEUTRO;
    };
  }

  private void persistirAnalise(SentimentRequest request, SentimentResponse response) {
    try {
      repository.save(mapper.toEntity(request, response));
      log.debug("Análise persistida com sucesso");
    } catch (Exception e) {
      log.error("Erro ao salvar no banco: {}", e.getMessage());
    }
  }
}