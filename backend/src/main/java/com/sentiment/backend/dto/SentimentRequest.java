package com.sentiment.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SentimentRequest {

    @NotBlank(message = "O campo 'text' é obrigatório e não pode estar vazio.")
    @Size(min = 4, message = "O texto deve ter pelo menos 4 caracteres para uma análise consistente.")
    @Size(max = 5000, message = "O texto ultrapassou o limite máximo de 5000 caracteres.")
    private String text;

    public SentimentRequest() {}

    public SentimentRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}