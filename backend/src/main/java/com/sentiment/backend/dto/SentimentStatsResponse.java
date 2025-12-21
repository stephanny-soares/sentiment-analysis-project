package com.sentiment.backend.dto;

import com.sentiment.backend.model.SentimentType;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SentimentStatsResponse {

    private final SentimentType sentimento;
    private final long quantidade;
    private final double percentual;

    public SentimentStatsResponse(SentimentType sentimento, long quantidade, double total) {
        this.sentimento = sentimento;
        this.quantidade = quantidade;
        this.percentual = calcularPercentual(quantidade, total);
    }

    private double calcularPercentual(long qtd, double total) {
        if (total == 0) return 0.0;
        double valor = (qtd * 100.0) / total;
        // Arredonda para 2 casas decimais
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // Apenas Getters (Imutável)
    public SentimentType getSentimento() { return sentimento; }
    public long getQuantidade() { return quantidade; }
    public double getPercentual() { return percentual; }
}