package com.sentiment.backend.model;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    private String prioridade;
    private String setor;
    private String sugestaoResposta;

    @ElementCollection
    @CollectionTable(name = "sentiment_tags", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SentimentAnalysis() {}

    public SentimentAnalysis(SentimentRequest request, SentimentResponse response) {
        this.text = request.getText();
        this.prediction = response.getPrevisao();
        this.confidence = response.getProbabilidade();
        this.prioridade = response.getPrioridade();
        this.setor = response.getSetor();
        this.sugestaoResposta = response.getSugestaoResposta();
        this.tags = response.getTags();
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
    public String getPrioridade() { return prioridade; }
    public String getSetor() { return setor; }
    public String getSugestaoResposta() { return sugestaoResposta; }
    public List<String> getTags() { return tags; }
}