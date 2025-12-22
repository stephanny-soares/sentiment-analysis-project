package com.sentiment.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sentiment.backend.model.SentimentType;

import java.util.List;

/**
 * DTO que representa a resposta da análise de sentimento.
 */
public class SentimentResponse {

    @JsonProperty("prediction")
    private final SentimentType previsao; // já é enum, não String

    @JsonProperty("confidence")
    private final Double probabilidade;

    private String prioridade;
    private List<String> tags;
    private String setor;
    private String sugestaoResposta;

    public SentimentResponse(SentimentType previsao, Double probabilidade,
                             String prioridade, List<String> tags,
                             String setor, String sugestaoResposta) {
        this.previsao = previsao;
        this.probabilidade = probabilidade;
        this.prioridade = prioridade;
        this.tags = tags;
        this.setor = setor;
        this.sugestaoResposta = sugestaoResposta;
    }

    public SentimentType getPrevisao() {
        return previsao;
    }
    public Double getProbabilidade() {
        return probabilidade;
    }
    public String getPrioridade() { return prioridade; }
    public List<String> getTags() { return tags; }
    public String getSetor() { return setor; }
    public String getSugestaoResposta() { return sugestaoResposta; }
}
