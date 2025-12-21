# Sentiment Analysis API - Marketing & CX Insights

O **Sentiment Analysis API** é uma solução inteligente desenvolvida para empresas que precisam monitorar o feedback dos clientes em larga escala. Através de uma abordagem híbrida que combina **Regras Gramaticais** e **Machine Learning**, transformamos textos brutos em dados estratégicos.

## Diferenciais da Solução
Nossa API não faz apenas uma contagem simples de palavras. Ela implementa um motor de análise robusto focado nas nuances da língua portuguesa:

- **Pivô de Sentimento (Lógica de Adversativas):** Identifica conjunções como "mas" e "porém", entendendo que a opinião final do autor geralmente vem após essas palavras.
- **Radicalização Ativa (Stemming):** Normaliza automaticamente variações de gênero e plural (ex: "rápidas", "rápido", "rápidos" → todos identificados pelo radical "rapid").
- **Tratamento de Contexto:** Inverte a polaridade em casos de negação (ex: "não bom") e dobra a pontuação em casos de intensificadores (ex: "muito bom").
- **Dashboard Gerencial:** Endpoint exclusivo para estatísticas que retorna volumes e percentuais de satisfação.

## Tecnologias
- **Back-end:** Java 17, Spring Boot 3, Spring Data JPA
- **Data Science:** Python, Pandas, Scikit-Learn, TF-IDF + Logistic Regression
- **Banco de Dados:** H2 (In-memory) para persistência ágil durante o MVP

## Estrutura do Projeto
```plaintext
sentiment-analysis-project/  
├── ds/ # Inteligência: Notebooks e Modelos (.pkl)  
├── backend/ # API REST e Lógica de Negócio em Java  
│  ├── src/main/java/ # Endpoints, Service e Motor de Análise  
│  └── src/resources/ # Configuração de Banco de Dados e Logs  
├── data/ # Conjuntos de dados usados para treino (CSV/XLSX)  
└── docs/ # Exemplos de chamadas e documentação da API
```
## Endpoints do MVP

### 1. Analisar Comentário
POST /sentiment  
Recebe o texto e retorna a classificação processada.

**Payload:**  
```json
{ "text": "O atendimento foi um pouco lento, mas o produto é sensacional!" }
```
**Resposta:**  
```json
{ "previsao": "POSITIVO", "probabilidade": 0.85 }
```
### 2. Dashboard de Estatísticas
GET /sentiment/stats  
Retorna um resumo consolidado do banco de dados para criação de gráficos.

**Resposta:**  
```json
[  
{ "sentimento": "POSITIVO", "quantidade": 45, "percentual": 75.0 },  
{ "sentimento": "NEGATIVO", "quantidade": 10, "percentual": 16.6 },  
{ "sentimento": "NEUTRO", "quantidade": 5, "percentual": 8.4 }  
]
```
## Instalação e Execução

### 1. Camada de Inteligência (Data Science)
Nota: As instruções de execução do modelo Python (FastAPI/Flask) serão atualizadas pelo time de DS após a finalização do treinamento.  
O modelo deve ser exposto na porta 8000 para consumo do Back-end.

### 2. Back-End (Java)
Certifique-se de ter o JDK 17 e o Maven instalados.
```bash
cd backend  
mvn clean install  
mvn spring-boot:run
```
A API estará disponível em: http://localhost:8082

## Equipe
- **Back-end:** Stephanny Soares
- **Data Science:** []

Projeto desenvolvido para o Hackathon 2025 - Foco em automação de CX (Customer Experience).
