package com.sentiment.backend.service;

import com.sentiment.backend.model.SentimentType;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BusinessRuleService {

    // Mapas de palavras-chave para categorias (Setores)
    private static final Map<String, List<String>> DICIONARIO_SETORES = Map.of(
            "LOGÍSTICA", Arrays.asList("entrega", "atraso", "prazo", "correios", "chegou", "frete", "envio"),
            "FINANCEIRO", Arrays.asList("preço", "valor", "pagamento", "boleto", "estorno", "caro", "cartão"),
            "PRODUTO", Arrays.asList("qualidade", "quebrado", "estragado", "defeito", "material", "tamanho"),
            "ATENDIMENTO", Arrays.asList("suporte", "atendente", "demora", "responder", "chat", "telefone", "atendimento", "ligação", "o atendente")
    );

    // Palavras que indicam urgência crítica
    private static final List<String> ALERTAS_CRITICOS = Arrays.asList("procon", "justiça", "advogado", "processar", "reclame aqui", "polícia", "revoltado");

    public String identificarPrioridade(String texto, SentimentType sentimento) {
        if (sentimento != SentimentType.NEGATIVO) return "BAIXA";

        String textoMinusculo = texto.toLowerCase();
        for (String alerta : ALERTAS_CRITICOS) {
            if (textoMinusculo.contains(alerta)) return "CRÍTICA";
        }
        return "ALTA";
    }

    public String identificarSetor(String texto) {
        String textoMinusculo = texto.toLowerCase();
        for (Map.Entry<String, List<String>> entry : DICIONARIO_SETORES.entrySet()) {
            for (String palavra : entry.getValue()) {
                if (textoMinusculo.contains(palavra)) return entry.getKey();
            }
        }
        return "GERAL";
    }

    public List<String> extrairTags(String texto) {
        List<String> tagsEncontradas = new ArrayList<>();
        String textoMinusculo = texto.toLowerCase();

        // Unifica todas as palavras de todos os setores para ver o que o cliente citou
        DICIONARIO_SETORES.values().forEach(lista -> {
            for (String p : lista) {
                if (textoMinusculo.contains(p) && !tagsEncontradas.contains(p)) {
                    tagsEncontradas.add(p);
                }
            }
        });
        return tagsEncontradas;
    }

    public String gerarSugestao(SentimentType sentimento, String setor) {
        if (sentimento == SentimentType.POSITIVO) return "Agradecemos o feedback! Ficamos felizes em ajudar.";
        if (sentimento == SentimentType.NEUTRO) return "Obrigado pelo comentário. Estamos à disposição.";

        return switch (setor) {
            case "LOGÍSTICA" -> "Lamentamos o transtorno com a entrega. Vamos verificar com a transportadora.";
            case "FINANCEIRO" -> "Sentimos muito pelo problema financeiro. Por favor, envie o comprovante para nosso setor de estorno.";
            default -> "Lamentamos sua experiência. Um atendente humano entrará em contato em breve.";
        };
    }
}