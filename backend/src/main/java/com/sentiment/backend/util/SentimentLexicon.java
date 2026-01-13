package com.sentiment.backend.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SentimentLexicon {

  public static final List<String> POSITIVAS = List.of(
      "bom", "bem", "otim", "excelen", "perfeit", "maravilh", "fantast", "sensacion",
      "agrad", "satisf", "eficient", "rapid", "impec", "top", "recomend",
      "util", "facil", "legal", "feliz", "vale", "surpreend",
      "atencios", "gentil", "educad", "profissional", "confiavel",
      "acert", "claro", "bonit", "limp", "qualific", "divertid", "eficaz");

  public static final List<String> NEGATIVAS = List.of(
      "ruim", "pessim", "lent", "demor", "deficien", "insuport", "horriv",
      "falh", "complic", "dific", "frustr", "terriv", "decepcion", "pior",
      "errad", "quebrad", "estrag", "caro", "absurd", "abus", "lixo",
      "pobr", "mal", "odi", "desrespeit", "atras", "suj", "insucess",
      "inaceit", "ineficient", "insatisf", "irrit", "inconvenient",
      "problem", "confus");

  public static final Set<String> ADVERSATIVAS = Set.of(
      "mas", "porem", "contudo", "entretanto", "todavia");

  public static final Set<String> NEGACOES = Set.of(
      "nao", "nunca", "jamais", "nem", "sem");

  public static final Set<String> INTENSIFICADORES = Set.of(
      "muito", "extremament", "super", "demais", "totalment", "altament", "bastante");
}
