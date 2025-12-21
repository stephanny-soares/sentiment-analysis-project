# Sentiment Analysis System - Marketing & CX Insights

Uma solução Full Stack para monitoramento de feedbacks em escala. O sistema integra um motor de análise de sentimentos a um dashboard gerencial, focado em transformar comentários brutos em dados prontos para a tomada de decisão em Customer Experience (CX).

## Visão Estratégica

Este repositório centraliza toda a esteira de dados: desde o processamento de texto com NLP no Back-end até a visualização simplificada no Front-end. O objetivo é eliminar a análise manual de feedbacks, extraindo automaticamente a percepção do cliente sobre a marca de forma ágil e padronizada.

---

## Estrutura do Repositório
```plaintext
sentiment-analysis-project/
├── backend/        API REST e Motor de Análise (Java/Spring Boot)
├── frontend/       Interface de Usuário e Dashboard (HTML/Tailwind)
├── ds/             Modelos de Machine Learning e Processamento de Dados
├── data/           Datasets de Treino e Validação
└── docs/           Documentação Técnica e Especificações
```
---

## Tecnologias e Diferenciais

### Back-end (Java 17 + Spring Boot 3)

- Motor de análise híbrido com:
  - tratamento de adversativas (ex: "mas", "porém")
  - radicalização de termos (stemming)
  - inversão de polaridade em frases com negação
- Persistência em banco H2, voltada para agilidade no MVP
- Configuração de CORS para integração com aplicações web

### Front-end (HTML5 + Tailwind CSS)

- Interface simples e funcional, focada em leitura rápida dos resultados
- Integração direta com a API REST via Fetch API

### Data Science (Python + Scikit-Learn)

- Modelo de Machine Learning baseado em TF-IDF + Logistic Regression
- Classificação automática de sentimentos com foco em precisão e escalabilidade

---

## Como Executar

### Back-end

1. Acesse o diretório backend
2. Execute:
```bash
   mvn clean install
   mvn spring-boot:run
```
3. A API estará disponível em:
```plaintext
   http://localhost:8082
```
### Front-end

Abrir o arquivo frontend/index.html em qualquer navegador moderno.

Observação: o back-end deve estar em execução para que a análise de sentimentos funcione corretamente na interface.

---

## Endpoints Principais
```plaintext
- POST /sentiment
  Analisa um texto e retorna a predição de sentimento.

- GET /sentiment/stats
  Retorna estatísticas consolidadas para uso em dashboards.
```
---

## Equipe

- Back-end, Front-end e Integração: Stephanny Soares
- Data Science: [  ]

---

#### Tecnologia aplicada à análise de sentimentos para otimização de fluxos de trabalho em Marketing e suporte à decisão em Customer Experience.
