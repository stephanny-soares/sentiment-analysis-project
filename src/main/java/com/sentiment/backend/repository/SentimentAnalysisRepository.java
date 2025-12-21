package com.sentiment.backend.repository;

import com.sentiment.backend.model.SentimentAnalysis;
import com.sentiment.backend.model.SentimentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório responsável por operações de persistência de análises de sentimento.
 */
@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {

    // Buscar todas as análises de um tipo específico
    List<SentimentAnalysis> findByPrediction(SentimentType prediction);

    // Buscar análises mais recentes, limitadas
    List<SentimentAnalysis> findTop10ByOrderByCreatedAtDesc();

    // Consultas paginadas por tipo de sentimento
    Page<SentimentAnalysis> findByPrediction(SentimentType prediction, Pageable pageable);
}
