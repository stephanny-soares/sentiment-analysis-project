![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-005850?style=for-the-badge&logo=fastapi&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
---
# Sentiment Analysis System 

Uma solução Full Stack projetada para transformar feedbacks de clientes em decisões estratégicas. O sistema utiliza uma arquitetura híbrida de microserviços, integrando o poder preditivo do Machine Learning (Python) com a robustez de um Motor de Regras de Negócio (Java/Spring Boot).

---

## Estrutura do Repositório
```plaintext
sentiment-analysis-project/
├── backend/           # API REST + Motor de Regras (Java/Spring Boot)
├── data_api/          # Microserviço de IA (Python/FastAPI)
├── frontend/          # Dashboard Administrativo (HTML + Tailwind CSS)
├── docker-compose.yml # Orquestração dos containers
└── README.md          # Documentação principal
```
---

## Tecnologias 

- Backend & Integração (Java)
  * Spring Boot 3.x: Orquestração da lógica de negócio.
  * Spring Data JPA: Persistência de dados (H2/MySQL).
  * RestTemplate: Comunicação síncrona com o serviço de IA.
  * Motor de Regras: Classificação automática de prioridade (Alta/Média/Baixa) e setor (Logística, Financeiro, etc.)

- Data Science & ML (Python 3.11)
  * FastAPI: Servidor de inferência de alta performance.
  * Scikit-Learn: Pipeline de NLP com TF-IDF e Regressão Logística.
  * Joblib: Persistência de modelos treinados.
  * Métricas: 88% de acurácia global com 96% de recall para comentários negativos.

- Frontend: HTML, Tailwind CSS;

- DevOps: Docker, Docker Compose;

---
## API Endpoints

```plaintext
POST /sentiment
```
Analisa um texto e retorna o objeto enriquecido com classificação, prioridade e contexto.

Exemplo de payload:
```plaintext
{"text": "O prazo de entrega não foi cumprido"}
```
Exemplo de resposta:
```json
{
  "prediction": "NEGATIVO",
  "confidence": 0.85,
  "prioridade": "ALTA",
  "setor": "LOGÍSTICA",
  "tags": ["atraso", "entrega"],
  "sugestaoResposta": "Lamentamos o problema. Nossa equipe de logística já foi acionada para verificar seu pedido."
}
```
---
```plaintext
GET /sentiment/stats
```
· Retorna métricas consolidadas para alimentação do dashboard.

---
# Como Executar

O projeto pode ser executado inteiro com Docker, ou localmente por serviço.

### Com Docker (recomendado)

Do diretório raiz do projeto
```plaintext
docker-compose up -d
```
Serviços disponíveis:
```plaintext
Backend: http://localhost:8082
Python API: http://localhost:5000
Frontend: http://localhost:8080
```
### Python (data_api)
```plaintext
cd data_api
python -m venv venv
source venv/bin/activate   # Linux/Mac
venv\Scripts\activate      # Windows
pip install -r requirements.txt
python sentiment_api.py
```
### Backend (Java/Spring Boot)
```plaintext
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend

Abrir frontend/index.html em navegador moderno (Chrome/Edge/Firefox)

---
# Fluxo de Dados (End-to-End)
- Entrada: O usuário insere um comentário no Frontend.

- Processamento ML: O Backend envia o texto para o data_api via POST. A IA devolve a classe ("Positivo", "Negativo" ou "Neutro").

- Regras de Negócio: O Backend recebe a classe da IA e aplica:

  * Priorização: Feedbacks negativos geram prioridade "ALTA".
 
  * Categorização: Busca termos-chave para definir o setor responsável.

  * Sugestão: Gera uma resposta automática baseada no sentimento.

- Saída: Os dados enriquecidos são salvos no banco e exibidos no dashboard.
---

## Equipe

Back-end, Front-end e Integração:
- https://github.com/stephanny-soares
- https://github.com/MrClaro

Data Science:
- https://github.com/emanuelssergio
- https://github.com/kaio326

---
## Links relacionados

Data Science do projeto: https://github.com/kaio326/hackathon-nocountry-one-alura

---

#### Solução orientada à análise de sentimentos para otimização de fluxos de trabalho em Marketing e suporte à decisão em Customer Experience.
 ---