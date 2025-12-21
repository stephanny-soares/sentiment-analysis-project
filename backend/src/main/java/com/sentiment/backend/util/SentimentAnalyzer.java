package com.sentiment.backend.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por analisar texto e retornar sentimento.
 */
public class SentimentAnalyzer {

    public static SentimentAnalysisResult analisar(String texto) {
        if (texto == null || texto.isBlank()) {
            return new SentimentAnalysisResult(
                    SentimentAnalysisResult.SentimentType.NEUTRO, 0, 0.5
            );
        }

        // Normalização: lowercase, remoção de acentos e caracteres inválidos
        String normalizedText = Normalizer
                .normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^a-z\\s]", "");

        // Tokenização e radicalização
        String[] palavras = normalizedText.split("\\s+");
        List<String> lemmas = new ArrayList<>();
        for (String p : palavras) lemmas.add(normalizeWord(p));

        double scoreTotal = 0;
        boolean efeitoAdversativa = false;

        for (int i = 0; i < lemmas.size(); i++) {
            String palavra = lemmas.get(i);

            // 1. LÓGICA DE PIVÔ (MAS, PORÉM, etc.)
            if (SentimentLexicon.ADVERSATIVAS.contains(palavra)) {
                scoreTotal *= 0.3;      // Reduz o impacto do que veio antes
                efeitoAdversativa = true;
                continue;
            }

            // 2. NEGAÇÃO (até 2 palavras atrás)
            boolean negacao = (i > 0 && SentimentLexicon.NEGACOES.contains(lemmas.get(i - 1))) ||
                    (i > 1 && SentimentLexicon.NEGACOES.contains(lemmas.get(i - 2)));

            // 3. INTENSIFICADOR (palavra imediatamente anterior)
            boolean intensificador = i > 0 && SentimentLexicon.INTENSIFICADORES.contains(lemmas.get(i - 1));

            double pesoImportancia = efeitoAdversativa ? 1.5 : 1.0;
            double pesoBase = (intensificador ? 2.0 : 1.0) * pesoImportancia;

            boolean ehPositivo = SentimentLexicon.POSITIVAS.stream().anyMatch(palavra::startsWith);
            boolean ehNegativo = SentimentLexicon.NEGATIVAS.stream().anyMatch(palavra::startsWith);

            if (ehPositivo) {
                scoreTotal += (negacao ? -pesoBase : pesoBase);
            } else if (ehNegativo) {
                scoreTotal += (negacao ? pesoBase : -pesoBase);
            }
        }

        // Determinar tipo de sentimento
        SentimentAnalysisResult.SentimentType tipo;
        if (scoreTotal > 0.2) tipo = SentimentAnalysisResult.SentimentType.POSITIVO;
        else if (scoreTotal < -0.2) tipo = SentimentAnalysisResult.SentimentType.NEGATIVO;
        else tipo = SentimentAnalysisResult.SentimentType.NEUTRO;

        // Calcular probabilidade
        double probabilidade = 0.5 + 0.1 * Math.min(Math.abs(scoreTotal), 5);
        probabilidade = Math.min(1.0, probabilidade);

        return new SentimentAnalysisResult(tipo, (int)scoreTotal, probabilidade);
    }

    /**
     * Normaliza palavra, remove plural/gênero e acentos
     */
    public static String normalizeWord(String word) {
        String w = Normalizer.normalize(word.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^a-z]", "");

        if (w.length() <= 3) return w;

        // Remove plural e vogal temática
        if (w.endsWith("s")) w = w.substring(0, w.length() - 1);
        if (w.endsWith("a") || w.endsWith("o") || w.endsWith("e")) w = w.substring(0, w.length() - 1);

        return w;
    }
}
