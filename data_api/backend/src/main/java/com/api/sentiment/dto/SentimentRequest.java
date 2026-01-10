package com.api.sentiment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentRequest {
    
    @NotBlank(message = "Texto não pode estar vazio")
    @Size(min = 3, max = 5000, message = "Texto deve ter entre 3 e 5000 caracteres")
    private String text;
    
    @Min(value = 1, message = "Rating mínimo é 1")
    @Max(value = 5, message = "Rating máximo é 5")
    private Integer rating;
    
    private Boolean recommendToFriend;
}
