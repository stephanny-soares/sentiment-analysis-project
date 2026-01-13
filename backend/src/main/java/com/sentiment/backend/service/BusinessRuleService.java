package com.sentiment.backend.service;

import com.sentiment.backend.config.BusinessRulesProperties;
import com.sentiment.backend.model.SentimentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessRuleService {

  private final BusinessRulesProperties properties;

  public String identificarPrioridade(String texto, SentimentType sentimento) {
    if (sentimento != SentimentType.NEGATIVO) {
      return "BAIXA";
    }

    if (contemAlertaCritico(texto)) {
      log.warn("Alerta crítico detectado");
      return "CRÍTICA";
    }

    return "ALTA";
  }

  public String identificarSetor(String texto) {
    String textoLower = texto.toLowerCase();

    return properties.getSectors().entrySet().stream()
        .filter(entry -> contemPalavraChave(textoLower, entry.getValue().getKeywords()))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse("GERAL");
  }

  public List<String> extrairTags(String texto) {
    String textoLower = texto.toLowerCase();

    return properties.getSectors().values().stream()
        .flatMap(config -> config.getKeywords().stream())
        .filter(textoLower::contains)
        .distinct()
        .limit(5)
        .collect(Collectors.toList());
  }

  public String gerarSugestao(SentimentType sentimento, String setor) {
    if (sentimento == SentimentType.POSITIVO) {
      return obterTemplate(setor, "positivo");
    }

    if (sentimento == SentimentType.NEUTRO) {
      return obterTemplate(setor, "neutro");
    }

    return obterTemplate(setor, "negativo");
  }

  private boolean contemAlertaCritico(String texto) {
    String textoLower = texto.toLowerCase();
    return properties.getCriticalAlerts().stream()
        .anyMatch(textoLower::contains);
  }

  private boolean contemPalavraChave(String texto, List<String> keywords) {
    return keywords.stream().anyMatch(texto::contains);
  }

  private String obterTemplate(String setor, String tipoSentimento) {
    BusinessRulesProperties.ResponseTemplate template = properties.getResponseTemplates().get(setor.toUpperCase());

    if (template == null) {
      template = properties.getResponseTemplates().get("GERAL");
    }

    return switch (tipoSentimento) {
      case "positivo" -> template.getPositivo();
      case "neutro" -> template.getNeutro();
      case "negativo" -> template.getNegativo();
      default -> template.getNeutro();
    };
  }
}
