package com.sentiment.backend.util;

/**
 * Resultado interno da análise de sentimento.
 * Contém o tipo, score e probabilidade.
 */
public class SentimentAnalysisResult {

    public enum SentimentType {
        POSITIVO,
        NEUTRO,
        NEGATIVO
    }

    private final SentimentType type; // Tipo do sentimento
    private final int score;           // Score calculado
    private final double probability;  // Probabilidade estimada (0.0 a 1.0)

    // Construtor
    public SentimentAnalysisResult(SentimentType type, int score, double probability) {
        this.type = type;
        this.score = score;
        this.probability = probability;
    }

    // Getters
    public SentimentType getType() { return type; }
    public int getScore() { return score; }
    public double getProbability() { return probability; }
}
