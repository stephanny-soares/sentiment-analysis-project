package com.sentiment.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sentiment.backend.model.SentimentType;

/**
 * DTO que representa a resposta da análise de sentimento.
 */
public class SentimentResponse {

    @JsonProperty("prediction")
    private final SentimentType previsao; // já é enum, não String

    @JsonProperty("confidence")
    private final Double probabilidade;

    public SentimentResponse(SentimentType previsao, Double probabilidade) {
        this.previsao = previsao;
        this.probabilidade = probabilidade;
    }

    public SentimentType getPrevisao() {
        return previsao;
    }

    public Double getProbabilidade() {
        return probabilidade;
    }
}
