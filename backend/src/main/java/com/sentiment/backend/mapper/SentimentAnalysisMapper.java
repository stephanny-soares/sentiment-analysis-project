
package com.sentiment.backend.mapper;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.model.SentimentAnalysis;
import org.springframework.stereotype.Component;

@Component
public class SentimentAnalysisMapper {

  public SentimentAnalysis toEntity(SentimentRequest request, SentimentResponse response) {
    return SentimentAnalysis.builder()
        .text(request.getText())
        .prediction(response.getPrevisao())
        .confidence(response.getProbabilidade())
        .prioridade(response.getPrioridade())
        .setor(response.getSetor())
        .sugestaoResposta(response.getSugestaoResposta())
        .tags(response.getTags())
        .build();
  }

  public SentimentResponse toResponse(SentimentAnalysis entity) {
    return SentimentResponse.builder()
        .previsao(entity.getPrediction())
        .probabilidade(entity.getConfidence())
        .prioridade(entity.getPrioridade())
        .setor(entity.getSetor())
        .sugestaoResposta(entity.getSugestaoResposta())
        .tags(entity.getTags())
        .build();
  }
}
