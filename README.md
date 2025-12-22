# Sentiment Analysis System - Marketing & CX Insights

Uma solução Full Stack de nível empresarial para monitoramento de feedbacks. O sistema integra inteligência artificial a um **Motor de Regras de Negócio (Business Intelligence)** para transformar comentários brutos em decisões estratégicas de Customer Experience (CX).

## Visão Estratégica

Diferente de classificadores simples, esta solução realiza a triagem automática e priorização de atendimentos. O sistema identifica não apenas o "clima" do cliente, mas também a urgência operacional e o setor responsável, reduzindo drasticamente o tempo de resposta (SLA) para casos críticos.

---

## Estrutura do Projeto
- **/backend**: API REST robusta em Java/Spring Boot com motor de regras integrado.
- **/frontend**: Dashboard interativo com Tailwind CSS e sinalização visual de urgência.
- **/ds**: Pipeline de Data Science (Python/Scikit-Learn) para classificação de sentimentos.

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

### Back-end (Inteligência de Negócio)
- **Priorização Automática**: Identifica menções a órgãos de defesa do consumidor (Procon) ou termos jurídicos, elevando o status para **Prioridade Crítica** instantaneamente.
- **Categorização por Setores**: Classifica os feedbacks entre Logística, Financeiro, Produto ou Atendimento via mapeamento de palavras-chave.
- **Extração de Tags**: Gera metadados (#ATRASO, #ESTORNO) para análises de causa raiz.
- **Sugestão de Resposta**: Engine que gera templates de resposta baseados no sentimento e setor, agilizando o trabalho do atendente.

### Front-end (Dashboard Gerencial)
- **Sinalização Visual**: Alerta pulsante para casos críticos e cards coloridos por sentimento.
- **Estatísticas em Tempo Real**: Gráficos dinâmicos de distribuição de satisfação via Chart.js.
- **Histórico Inteligente**: Visualização organizada por contexto de setor e urgência.

### API Endpoints
```plaitext
 POST `/sentiment`
```
Analisa um texto e retorna o enriquecimento completo de dados.
**Exemplo de Resposta:**
```json
{
  "prediction": "NEGATIVO",
  "confidence": 0.98,
  "prioridade": "CRÍTICA",
  "setor": "LOGÍSTICA",
  "tags": ["entrega", "atraso", "procon"],
  "sugestaoResposta": "Lamentamos o transtorno com a entrega. Vamos verificar com a transportadora agora mesmo."
}
```
```plaitext
GET /sentiment/stats
```
· Retorna métricas consolidadas para o dashboard.


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
