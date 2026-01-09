package com.api.sentiment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResponse {
    
    @JsonProperty("previsao")
    private String previsao;
    
    @JsonProperty("probabilidade")
    private Double probabilidade;
    
    @JsonProperty("mensagem")
    private String mensagem;
    
    @JsonProperty("modelo_usado")
    private String modeloUsado;
}
