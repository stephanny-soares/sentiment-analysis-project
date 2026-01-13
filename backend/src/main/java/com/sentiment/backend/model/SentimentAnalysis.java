package com.sentiment.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sentiment_analysis", indexes = {
    @Index(name = "idx_prediction", columnList = "prediction"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class SentimentAnalysis {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 5000, columnDefinition = "TEXT")
  private String text;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SentimentType prediction;

  @Column(nullable = false)
  private Double confidence;

  @Column(length = 20)
  private String prioridade;

  @Column(length = 100)
  private String setor;

  @Column(length = 1000, columnDefinition = "TEXT")
  private String sugestaoResposta;

  @ElementCollection
  @CollectionTable(name = "sentiment_tags", joinColumns = @JoinColumn(name = "analysis_id"))
  @Column(name = "tag", length = 50)
  private List<String> tags;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (this.createdAt == null) {
      this.createdAt = LocalDateTime.now();
    }
  }
}
