# Sentiment Analysis System - Marketing & CX Insights

Uma solução Full Stack Enterprise para monitoramento de feedbacks. O sistema integra modelos de Machine Learning (Python) a um Motor de Regras de Negócio (Java/Spring Boot) via comunicação REST, transformando comentários brutos em decisões estratégicas de Customer Experience (CX).

## Diferencial Técnico: Arquitetura Híbrida

Diferentemente de classificadores de sentimento isolados, este sistema:

- Centraliza a inteligência de negócio no back-end
- Automatiza a priorização de atendimentos críticos
- Reduz o tempo de resposta (SLA) para casos sensíveis
- Permite análises históricas e métricas de satisfação
- Suporta integração com dashboards gerenciais

---

## Estrutura do Projeto
- **/backend**: API REST em Java/Spring Boot com motor de regras, persistência e integração com o serviço de IA.
- **/frontend**: Dashboard gerencial com sinalização visual de urgência e indicadores de sentimento.
- **/ai_models**: Microserviço em Python/Flask responsável pela inferência dos modelos de Machine Learning.

---

## Estrutura do Repositório
```plaintext
sentiment-analysis-project/
├── backend/        API REST e Motor de Análise (Java/Spring Boot)
├── frontend/       Dashboard Web (HTML / Tailwind CSS)
└── ai_models/      API Flask e Modelos Preditivos (.pkl)
```
---

## Tecnologias e Diferenciais

### Inteligência Artificial (Python)
- Modelos de Machine Learning treinados com scikit-learn
- Processamento de linguagem natural (NLP)
- API Flask atuando como servidor de inferência
- Comunicação via HTTP REST

### Back-end – Inteligência de Negócio (Java)

- Integração REST com o microserviço de IA
- Aplicação de regras de negócio para:
  - Priorização automática de feedbacks críticos
  - Identificação de termos jurídicos ou sensíveis
  - Classificação por setor (Logística, Financeiro, Produto, Atendimento)
- Extração automática de tags para análise de causa raiz
- Geração de sugestões de resposta baseadas em contexto
- Persistência histórica para auditoria e métricas

### Front-end 
- Visualização consolidada dos feedbacks analisados
- Sinalização visual de urgência e sentimento
- Gráficos estatísticos de distribuição de sentimentos
- Histórico organizado por setor e nível de prioridade

---
## API Endpoints

```plaintext
POST /sentiment
```
Analisa um texto e retorna o enriquecimento completo de dados (ML + Motor de Regras).

**Exemplo de Resposta:**
```json
{
  "prediction": "NEGATIVO",
  "confidence": 0.85,
  "prioridade": "ALTA",
  "setor": "LOGÍSTICA",
  "tags": ["atraso", "entrega"],
  "sugestaoResposta": "Lamentamos o problema com sua entrega. Nossa equipe de logística já foi acionada."
}
```
```plaintext
**GET /sentiment/stats**
```
· Retorna métricas consolidadas para alimentação do dashboard.

---

## Data Science (Python + Scikit-Learn)

- **Processamento**: Pipeline baseado em TF-IDF para vetorização de texto.
- **Algoritmo**: Regressão Logística treinada para classificação automática.
- **Escalabilidade**: Modelos exportados em formato Joblib (.pkl) para consumo em tempo real via microserviço.
---

## Como Executar

### Serviço de IA (Python)

Acesse o diretório ai_models, ative o ambiente virtual e execute o servidor Flask:
```plaintext
python app.py
```
O serviço estará disponível em http://localhost:5000

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

## Equipe

- Back-end, Front-end e Integração: [  ]
- Data Science: [  ]

---

#### Solução orientada à análise de sentimentos para otimização de fluxos de trabalho em Marketing e suporte à decisão em Customer Experience.
