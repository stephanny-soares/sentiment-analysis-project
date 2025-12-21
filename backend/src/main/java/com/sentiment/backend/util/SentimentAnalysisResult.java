package com.sentiment.backend.util;

/**
 * Resultado interno da análise de sentimento.
 */
public class SentimentAnalysisResult {

    public enum SentimentType {
        POSITIVO,
        NEUTRO,
        NEGATIVO
    }

    private final SentimentType type;
    private final int score;
    private final double probability;

    public SentimentAnalysisResult(SentimentType type, int score, double probability) {
        this.type = type;
        this.score = score;
        this.probability = probability;
    }

    public SentimentType getType() { return type; }
    public int getScore() { return score; }

    // Altere este nome ou adicione este como um "alias"
    public double getProbabilidade() { return probability; }

    public double getProbability() { return probability; }
}