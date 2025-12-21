package com.sentiment.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO que representa a requisição de análise de sentimento.
 */
public class SentimentRequest {

    @NotBlank(message = "O texto não pode estar vazio")
    @Size(max = 5000, message = "O texto não pode ter mais de 5000 caracteres")
    private String text;  // remover "final"

    // Construtor padrão (obrigatório para Jackson)
    public SentimentRequest() {}

    // Construtor com argumento (opcional)
    public SentimentRequest(String text) {
        this.text = text;
    }

    // Getter e Setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
