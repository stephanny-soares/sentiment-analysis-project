package com.sentiment.backend.repository;

import com.sentiment.backend.model.SentimentAnalysis;
import com.sentiment.backend.model.SentimentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {

    // Buscar as 10 análises mais recentes
    List<SentimentAnalysis> findTop10ByOrderByCreatedAtDesc();

    // Contagem para as estatísticas
    long countByPrediction(SentimentType prediction);

    // Buscas por tipo
    List<SentimentAnalysis> findByPrediction(SentimentType prediction);

    // Consultas paginadas
    Page<SentimentAnalysis> findByPrediction(SentimentType prediction, Pageable pageable);
}