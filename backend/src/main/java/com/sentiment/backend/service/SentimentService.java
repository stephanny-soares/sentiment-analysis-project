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

    SentimentType tipoSentimento = chamarIAPython(texto, request.getRating(), request.getRecommendToFriend());
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

  private Map<String, Object> respostaPython;

  private SentimentType chamarIAPython(String texto, Integer rating, Boolean recommendToFriend) {
    try {
      Map<String, Object> corpo = new HashMap<>();
      corpo.put("text", texto);

      // Se tiver rating e recommendToFriend, usar endpoint /auto (escolhe modelo automaticamente)
      boolean useEnhanced = rating != null && recommendToFriend != null;
      String url = useEnhanced ? PYTHON_URL_AUTO : PYTHON_URL;

      if (useEnhanced) {
        corpo.put("rating", rating);
        corpo.put("recommend_to_friend", recommendToFriend);
        log.debug("Usando modelo enhanced - Rating: {}, Recomenda: {}", rating, recommendToFriend);
      } else {
        log.debug("Usando modelo básico");
      }

      log.debug("Enviando texto para IA Python: {}", url);

      Map<String, Object> resposta = restTemplate.postForObject(url, corpo, Map.class);
      
      // Guardar a resposta completa da API para extrair confiança depois
      this.respostaPython = resposta;

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
    
    // Extrair confiança real da resposta Python, com fallback
    Double confianca = CONFIDENCE_DEFAULT;
    if (respostaPython != null && respostaPython.containsKey("probabilidade")) {
      Object prob = respostaPython.get("probabilidade");
      if (prob instanceof Number) {
        confianca = ((Number) prob).doubleValue();
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

  private void persistirAnalise(SentimentRequest request, SentimentResponse response) {
    SentimentAnalysis analysis = mapper.toEntity(request, response);
    repository.save(analysis);
    log.debug("Análise persistida com sucesso");
  }
}
