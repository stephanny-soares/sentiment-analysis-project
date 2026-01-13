# Sentiment Analysis API - Standalone

API independente de análise de sentimentos usando Machine Learning. Utilizável como microserviço em seu próprio backend.

## 🎯 Sobre este módulo

- **Modelos**: TF-IDF + Regressão Logística (original) e Random Forest (enhanced)
- **Classes**: Positivo, Neutro, Negativo
- **API**: FastAPI com documentação automática em /docs
- **Acurácia**: 88-92% (depende do modelo)

## 🤖 Modelos disponíveis

### Modelo Original
- **Algoritmo**: TF-IDF + Regressão Logística
- **Features**: Apenas texto
- **Acurácia**: ~88%
- **Quando usar**: Análise rápida e compatibilidade

### Modelo Enhanced (Recomendado)
- **Algoritmo**: TF-IDF + Random Forest
- **Features**: Texto + Rating (1-5) + Recomendação (Sim/Não) + Comprimento
- **Acurácia**: ~92% (4% melhor)
- **Quando usar**: Análise detalhada com mais informações

## 📊 Comparação de modelos

| Característica | Original | Enhanced |
|---|---|---|
| **Features** | Texto | Texto + Rating + Recomendação |
| **Algoritmo** | Regressão Logística | Random Forest |
| **Acurácia** | 88% | 92% |
| **Tempo de treinamento** | Rápido | Moderado |
| **Uso de memória** | Baixo | Moderado |

**Usar Enhanced quando:** Você tem rating e informação de recomendação disponíveis para decisões mais críticas.

## 🚀 Setup local para desenvolvimento

### Requisitos
- Python 3.11+
- pip

### Instalação

1. **Criar e ativar ambiente virtual**
   ```bash
   python3 -m venv .venv
   source .venv/bin/activate
   ```

2. **Instalar dependências**
   ```bash
   pip install -r requirements.txt
   ```

3. **Rodar a API**
   ```bash
   python enhanced_sentiment_api.py 8000
   ```

Acesse: http://localhost:8000

## 🧠 Treinar modelo enhanced

Para treinar o modelo enhanced com melhor acurácia:

1. **Ir para pasta de notebooks**
   ```bash
   cd notebooks
   ```

2. **Executar o notebook**
   ```bash
   jupyter notebook enhanced_sentiment_model.ipynb
   ```

3. **Executar todas as células** para:
   - Carregar dados processados
   - Criar features TF-IDF
   - Adicionar rating, recomendação e comprimento
   - Treinar Random Forest
   - Salvar em `models/enhanced/`

**Resultado esperado:** ~92% acurácia (melhor que modelo original)

## 🧪 Testando a API

**Verificar saúde:**
```bash
curl http://localhost:8000/health
```

**Predição simples:**
```bash
curl -X POST http://localhost:8000/predict \
     -H "Content-Type: application/json" \
     -d '{"text": "produto excelente"}'
```

**Predição enhanced:**
```bash
curl -X POST http://localhost:8000/predict/enhanced \
     -H "Content-Type: application/json" \
     -d '{"text":"produto excelente","rating":5,"recommend_to_friend":true}'
```

**Documentação:** http://localhost:8000/docs

## 📁 Estrutura

```
data_science/
├── models/               # Modelos treinados
│   ├── tfidf_vectorizer.joblib
│   ├── logistic_regression_model.joblib
│   └── enhanced/         # Modelos avançados
|        ├── random_forest_model.joblib
|        ├── rating_scaler.joblib
|        ├── text_length_scaler.joblib
|        └── tfidf_vectorizer.joblib
├── datasets/             # Dados de treinamento
├── notebooks/            # Notebooks Jupyter
├── enhanced_sentiment_api.py  # API (principal)
├── requirements.txt
└── README.md
```

## 📊 Configuração do treinamento

- **Vectorizer**: TF-IDF com 1000 features (unigrams + bigrams)
- **Modelo**: Regressão Logística com pesos personalizados
- **Pesos**: Negativo (5.0), Neutro (1.0), Positivo (1.0)
- **Foco**: Detectar comentários negativos

**Métricas:**
- Acurácia: 88%
- Recall (Negativos): 96%
- F1-Score: 84%

## 🧪 Endpoints da API

- `POST /predict` - Modelo original (apenas texto)
- `POST /predict/enhanced` - Modelo enhanced (texto + rating + recomendação)
- `POST /predict/auto` - Seleciona modelo automaticamente
- `GET /health` - Verificar saúde da API
- `GET /docs` - Documentação interativa

## 🐛 Troubleshooting
