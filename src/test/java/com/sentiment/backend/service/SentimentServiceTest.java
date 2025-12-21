package com.sentiment.backend.service;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.model.SentimentType;
import com.sentiment.backend.repository.SentimentAnalysisRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SentimentServiceTest {

    @Mock
    private SentimentAnalysisRepository repository;

    @InjectMocks
    private SentimentService service;

    @Test
    void deveRetornarSentimentoPositivo() {
        SentimentRequest request = new SentimentRequest("Hoje o dia foi muito bom!");

        SentimentResponse response = service.analisarSentimento(request);

        assertEquals(SentimentType.POSITIVO, response.getPrevisao());
    }

    @Test
    void deveRetornarSentimentoNegativo() {
        SentimentRequest request = new SentimentRequest("O filme não foi bom");

        SentimentResponse response = service.analisarSentimento(request);

        assertEquals(SentimentType.NEGATIVO, response.getPrevisao());
    }

    @Test
    void deveRetornarSentimentoNeutro() {
        SentimentRequest request = new SentimentRequest("A reunião foi normal");

        SentimentResponse response = service.analisarSentimento(request);

        assertEquals(SentimentType.NEUTRO, response.getPrevisao());
    }
}
