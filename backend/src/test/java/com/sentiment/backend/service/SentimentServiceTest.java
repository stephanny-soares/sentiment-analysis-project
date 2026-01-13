package com.sentiment.backend.service;

import com.sentiment.backend.dto.SentimentRequest;
import com.sentiment.backend.dto.SentimentResponse;
import com.sentiment.backend.model.SentimentType;
import com.sentiment.backend.repository.SentimentAnalysisRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SentimentServiceTest {

    @Mock
    private SentimentAnalysisRepository repository;

    @Mock
    private BusinessRuleService businessRuleService; // <--- ADICIONE ESTE MOCK

    @InjectMocks
    private SentimentService service;

    private void configurarMocksDeNegocio(SentimentType tipo) {
        // Configura comportamentos padrão para o motor de regras não retornar null nos testes
        when(businessRuleService.identificarPrioridade(anyString(), any())).thenReturn("NORMAL");
        when(businessRuleService.identificarSetor(anyString())).thenReturn("GERAL");
        when(businessRuleService.extrairTags(anyString())).thenReturn(Collections.emptyList());
        when(businessRuleService.gerarSugestao(any(), anyString())).thenReturn("Sugestão de teste");
    }

    @Test
    void deveRetornarSentimentoPositivo() {
        configurarMocksDeNegocio(SentimentType.POSITIVO);
        SentimentRequest request = SentimentRequest.builder()
            .text("Hoje o dia foi muito bom!")
            .build();

        SentimentResponse response = service.analisarSentimento(request);

        assertEquals(SentimentType.POSITIVO, response.getPrevisao());
    }

    @Test
    void deveRetornarSentimentoNegativo() {
        configurarMocksDeNegocio(SentimentType.NEGATIVO);
        SentimentRequest request = SentimentRequest.builder()
            .text("O filme não foi bom")
            .build();

        SentimentResponse response = service.analisarSentimento(request);

        assertEquals(SentimentType.NEGATIVO, response.getPrevisao());
    }

    @Test
    void deveRetornarSentimentoNeutro() {
        configurarMocksDeNegocio(SentimentType.NEUTRO);
        SentimentRequest request = SentimentRequest.builder()
            .text("A reunião foi normal")
            .build();

        SentimentResponse response = service.analisarSentimento(request);

        assertEquals(SentimentType.NEUTRO, response.getPrevisao());
    }
}