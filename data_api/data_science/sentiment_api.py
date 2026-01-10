"""
FastAPI Microserviço para Sentiment Analysis
Carrega o modelo treinado e expõe endpoint para predições
"""

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field, ConfigDict
import joblib
import json
import numpy as np
from typing import Dict, List
import uvicorn

# ============================================================================
# INICIALIZAÇÃO
# ============================================================================

app = FastAPI(
    title="Sentiment Analysis API",
    description="API para análise de sentimentos usando TF-IDF + Logistic Regression",
    version="1.0.0"
)

# Carregar modelos e configurações
print("📁 Carregando modelos...")

tfidf_vectorizer = joblib.load('data_science/models/tfidf_vectorizer.joblib')
model = joblib.load('data_science/models/logistic_regression_model.joblib')

with open('data_science/models/sentiment_mapping.json', 'r') as f:
    sentiment_mapping = json.load(f)

with open('data_science/models/model_metadata.json', 'r') as f:
    metadata = json.load(f)

# Criar mapeamento reverso
reverse_mapping = {v: k for k, v in sentiment_mapping.items()}

print("✅ Modelos carregados com sucesso!")
print(f"   Acurácia do modelo: {metadata['accuracy_test']:.4f}")
print(f"   Classes: {metadata['classes']}")


# ============================================================================
# MODELOS PYDANTIC
# ============================================================================

class SentimentRequest(BaseModel):
    """Modelo para requisição de predição"""
    text: str = Field(..., min_length=3, max_length=5000, description="Texto para análise")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "text": "Este produto é excelente! Recomendo!"
            }
        }
    )


class SentimentResponse(BaseModel):
    """Modelo para resposta de predição"""
    previsao: str = Field(..., description="Sentimento predito")
    probabilidade: float = Field(..., ge=0, le=1, description="Confiança da predição")
    probabilidades_detalhadas: Dict[str, float] = Field(..., description="Probabilidades para cada classe")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "previsao": "Positivo",
                "probabilidade": 0.92,
                "probabilidades_detalhadas": {
                    "Negativo": 0.03,
                    "Neutro": 0.05,
                    "Positivo": 0.92
                }
            }
        }
    )


class HealthResponse(BaseModel):
    """Modelo para resposta de health check"""
    status: str
    accuracy: float
    classes: List[str]


class BulkSentimentRequest(BaseModel):
    """Modelo para requisição em lote"""
    texts: List[str] = Field(..., description="Lista de textos para análise")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "texts": [
                    "Ótimo produto!",
                    "Péssimo atendimento",
                    "Produto OK"
                ]
            }
        }
    )


class BulkSentimentResponse(BaseModel):
    """Modelo para resposta em lote"""
    resultados: List[SentimentResponse]
    total: int


# ============================================================================
# ENDPOINTS
# ============================================================================

@app.get("/health", response_model=HealthResponse, tags=["Health"])
async def health_check():
    """
    Verifica a saúde da API e retorna informações do modelo
    """
    return {
        "status": "OK",
        "accuracy": metadata['accuracy_test'],
        "classes": metadata['classes']
    }


@app.post("/predict", response_model=SentimentResponse, tags=["Predictions"])
async def predict_sentiment(request: SentimentRequest):
    """
    Realiza predição de sentimento para um texto único
    
    **Parâmetros:**
    - text: String com o texto para análise (mínimo 3 caracteres)
    
    **Retorna:**
    - previsao: Sentimento (Positivo, Negativo ou Neutro)
    - probabilidade: Confiança da predição (0-1)
    - probabilidades_detalhadas: Probabilidades para cada sentimento
    """
    try:
        # Validação robusta
        if not request.text or len(request.text.strip()) < 3:
            raise HTTPException(
                status_code=400,
                detail="Texto deve ter no mínimo 3 caracteres"
            )
        
        if len(request.text) > 10000:
            raise HTTPException(
                status_code=400,
                detail="Texto deve ter no máximo 10.000 caracteres"
            )
        
        # Sanitização básica
        text = request.text.strip()
        
        # Vetorizar
        text_tfidf = tfidf_vectorizer.transform([text])
        
        # Predizer
        pred_label = model.predict(text_tfidf)[0]
        pred_proba = model.predict_proba(text_tfidf)[0]
        
        sentiment = reverse_mapping[pred_label]
        confidence = float(np.max(pred_proba))
        
        # Criar dicionário de probabilidades detalhadas
        probabilidades_detalhadas = {
            reverse_mapping[i]: float(pred_proba[i])
            for i in range(len(pred_proba))
        }
        
        return {
            "previsao": sentiment,
            "probabilidade": confidence,
            "probabilidades_detalhadas": probabilidades_detalhadas
        }
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro ao processar: {str(e)}"
        )


@app.post("/predict_bulk", response_model=BulkSentimentResponse, tags=["Predictions"])
async def predict_sentiment_bulk(request: BulkSentimentRequest):
    """
    Realiza predição de sentimento para múltiplos textos
    
    **Parâmetros:**
    - texts: Lista de strings para análise
    
    **Retorna:**
    - resultados: Lista com predições para cada texto
    - total: Total de textos processados
    """
    try:
        if not request.texts:
            raise HTTPException(
                status_code=400,
                detail="Lista de textos não pode estar vazia"
            )
        
        if len(request.texts) > 100:
            raise HTTPException(
                status_code=400,
                detail="Máximo de 100 textos por requisição"
            )
        
        resultados = []
        
        for text in request.texts:
            if not text or len(text.strip()) < 3:
                resultados.append({
                    "previsao": "Erro",
                    "probabilidade": 0.0,
                    "probabilidades_detalhadas": {}
                })
                continue
            
            if len(text) > 10000:
                resultados.append({
                    "previsao": "Erro - Texto muito longo",
                    "probabilidade": 0.0,
                    "probabilidades_detalhadas": {}
                })
                continue
            
            # Vetorizar
            text_tfidf = tfidf_vectorizer.transform([text])
            
            # Predizer
            pred_label = model.predict(text_tfidf)[0]
            pred_proba = model.predict_proba(text_tfidf)[0]
            
            sentiment = reverse_mapping[pred_label]
            confidence = float(np.max(pred_proba))
            
            # Criar dicionário de probabilidades detalhadas
            probabilidades_detalhadas = {
                reverse_mapping[i]: float(pred_proba[i])
                for i in range(len(pred_proba))
            }
            
            resultados.append({
                "previsao": sentiment,
                "probabilidade": confidence,
                "probabilidades_detalhadas": probabilidades_detalhadas
            })
        
        return {
            "resultados": resultados,
            "total": len(request.texts)
        }
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro ao processar: {str(e)}"
        )


@app.get("/metadata", tags=["Info"])
async def get_metadata():
    """
    Retorna metadata do modelo
    """
    return metadata


@app.get("/", tags=["Info"])
async def root():
    """
    Endpoint raiz com informações da API
    """
    return {
        "titulo": "Sentiment Analysis API",
        "versao": "1.0.0",
        "descricao": "API para análise de sentimentos usando TF-IDF + Logistic Regression",
        "endpoints": {
            "health": "/health",
            "predict": "/predict",
            "predict_bulk": "/predict_bulk",
            "metadata": "/metadata",
            "docs": "/docs",
            "redoc": "/redoc"
        }
    }


# ============================================================================
# INICIAR SERVIDOR
# ============================================================================

if __name__ == "__main__":
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=5000,
        log_level="info"
    )
