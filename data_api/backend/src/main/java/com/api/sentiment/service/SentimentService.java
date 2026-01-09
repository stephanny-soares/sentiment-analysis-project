package com.api.sentiment.service;

import com.api.sentiment.dto.SentimentRequest;
import com.api.sentiment.dto.SentimentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SentimentService {

    @Value("${sentiment.api.url:http://localhost:5000}")
    private String sentimentApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SentimentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Chama o microserviço Python para predição de sentimento
     */
    public SentimentResponse predictSentiment(SentimentRequest request) {
        try {
            String url;
            Map<String, Object> payload = new HashMap<>();
            
            // Determinar qual endpoint usar baseado nos parâmetros
            if (request.getRating() != null && request.getRecommendToFriend() != null) {
                // Usar enhanced
                url = sentimentApiUrl + "/predict/enhanced";
                payload.put("text", request.getText());
                payload.put("rating", request.getRating());
                payload.put("recommend_to_friend", request.getRecommendToFriend());
                log.info("Usando modelo enhanced com rating e recomendação");
            } else if (request.getRating() != null || request.getRecommendToFriend() != null) {
                // Usar auto se apenas alguns parâmetros estão presentes
                url = sentimentApiUrl + "/predict/auto";
                payload.put("text", request.getText());
                if (request.getRating() != null) {
                    payload.put("rating", request.getRating());
                }
                if (request.getRecommendToFriend() != null) {
                    payload.put("recommend_to_friend", request.getRecommendToFriend());
                }
                log.info("Usando modelo auto com parâmetros parciais");
            } else {
                // Usar original (apenas texto)
                url = sentimentApiUrl + "/predict";
                payload.put("text", request.getText());
                log.info("Usando modelo original (apenas texto)");
            }
            
            log.info("Chamando microserviço Python: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String requestBody = objectMapper.writeValueAsString(payload);
            HttpEntity<String> httpRequest = new HttpEntity<>(requestBody, headers);
            
            // Fazer chamada
            String response = restTemplate.postForObject(url, httpRequest, String.class);
            
            // Parsear resposta
            JsonNode jsonNode = objectMapper.readTree(response);
            
            String previsao = jsonNode.get("previsao").asText();
            Double probabilidade = jsonNode.get("probabilidade").asDouble();
            String modeloUsado = jsonNode.has("modelo_usado") ? jsonNode.get("modelo_usado").asText() : "original";
            
            log.info("Resposta recebida: {} com probabilidade {} (modelo: {})", previsao, probabilidade, modeloUsado);
            
            return new SentimentResponse(
                previsao,
                probabilidade,
                "Análise concluída com sucesso",
                modeloUsado
            );

        } catch (Exception e) {
            log.error("Erro ao chamar microserviço Python: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar sentimento: " + e.getMessage(), e);
        }
    }
}
