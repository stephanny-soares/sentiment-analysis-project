package com.sentiment.backend.model;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma análise de sentimento persistida no banco.
 */
@Entity
@Table(name = "sentiment_analysis")
public class SentimentAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SentimentType prediction;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SentimentAnalysis() {}

    /**
     * Construtor principal.
     * @param text Texto analisado
     * @param prediction Tipo de sentimento
     * @param confidence Probabilidade/confiança da análise
     */
    public SentimentAnalysis(String text, SentimentType prediction, Double confidence) {
        this.text = text;
        this.prediction = prediction;
        this.confidence = confidence;
    }

    /**
     * Construtor a partir de DTOs (facilita persistência no banco)
     * @param request DTO de requisição
     * @param response DTO de resposta
     */
    public SentimentAnalysis(SentimentRequest request, SentimentResponse response) {
        this.text = request.getText();
        this.prediction = response.getPrevisao(); // já é enum SentimentType
        this.confidence = response.getProbabilidade();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getText() { return text; }
    public SentimentType getPrediction() { return prediction; }
    public Double getConfidence() { return confidence; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters (somente se necessário)
    public void setText(String text) { this.text = text; }
    public void setPrediction(SentimentType prediction) { this.prediction = prediction; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
