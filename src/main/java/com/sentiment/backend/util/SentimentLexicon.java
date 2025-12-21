package com.sentiment.backend.util;

import java.util.List;
import java.util.Set;

/**
 * Contém radicais, expressões, conjunções adversativas,
 * palavras de negação e intensificadores para análise de sentimento.
 */
public class SentimentLexicon {

    // Radicais Positivos
    public static final List<String> POSITIVAS = List.of(
            "bom", "bo", "otim", "excelen", "perfeit", "maravilh", "fantast", "sensacion",
            "agrad", "satisf", "eficient", "rapid", "impec", "top", "recomend",
            "util", "facil", "legal", "feliz", "vale", "surpreend",
            "atencios", "gentil", "educad", "profissional", "confiavel",
            "acert", "claro", "bonit", "limp", "qualific", "divertid", "eficaz"
    );

    // Radicais Negativos
    public static final List<String> NEGATIVAS = List.of(
            "ruim", "pessim", "lent", "demor", "deficien", "insuport", "horriv",
            "falh", "complic", "dific", "frustr", "terriv", "decepcion", "pior",
            "errad", "quebrad", "estrag", "caro", "absurd", "abus", "lixo",
            "pobr", "mal", "odi", "desrespeit", "atras", "suj", "insucess",
            "inaceit", "ineficient", "insatisf", "irrit", "inconvenient",
            "problem", "confus"
    );

    // Conjunções adversativas (mudam peso do que vem depois)
    public static final Set<String> ADVERSATIVAS = Set.of(
            "mas", "porem", "contudo", "entretanto", "todavia"
    );

    // Palavras de negação
    public static final Set<String> NEGACOES = Set.of(
            "nao", "nunca", "jamais", "nem", "sem"
    );

    // Intensificadores
    public static final Set<String> INTENSIFICADORES = Set.of(
            "muito", "extremament", "super", "demais", "totalment", "altament", "bastante"
    );

    // Expressões fixas positivas
    public static final List<String> EXPRESSOES_POSITIVAS = List.of(
            "agradavel atendimento", "otima experiencia", "excelente servico"
    );

    // Expressões fixas negativas
    public static final List<String> EXPRESSOES_NEGATIVAS = List.of(
            "nao gostei", "pessimo servico", "ruim atendimento", "horrivel experiencia"
    );
}
