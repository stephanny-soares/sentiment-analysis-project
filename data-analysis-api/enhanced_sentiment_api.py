"""
Enhanced Sentiment Analysis API
Suporta tanto modelo original (texto apenas) quanto enhanced (texto + rating + recomendação)
"""

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field, ConfigDict
from contextlib import asynccontextmanager
import joblib
import json
import numpy as np
from typing import Dict, List, Optional
import uvicorn
from scipy.sparse import hstack
import os
import sys

# ============================================================================
# INICIALIZAÇÃO
# ============================================================================

# Definir caminho base (diretório do script)
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODELS_DIR = os.path.join(BASE_DIR, 'models')
MODELS_ENHANCED_DIR = os.path.join(MODELS_DIR, 'enhanced')

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Lifecycle event handler para carregar modelos e mostrar endpoints"""
    # Startup
    global tfidf_original, model_original, sentiment_mapping, reverse_mapping
    global tfidf_enhanced, rating_scaler, text_length_scaler, model_enhanced
    global enhanced_metadata, ENHANCED_AVAILABLE
    
    # Carregar modelo original
    print("📁 Carregando modelo original...")
    tfidf_original = joblib.load(os.path.join(MODELS_DIR, 'tfidf_vectorizer.joblib'))
    model_original = joblib.load(os.path.join(MODELS_DIR, 'logistic_regression_model.joblib'))

    with open(os.path.join(MODELS_DIR, 'sentiment_mapping.json'), 'r') as f:
        sentiment_mapping = json.load(f)

    # Tentar carregar modelo enhanced (se existir)
    try:
        print("📁 Carregando modelo enhanced...")
        tfidf_enhanced = joblib.load(os.path.join(MODELS_ENHANCED_DIR, 'tfidf_vectorizer.joblib'))
        rating_scaler = joblib.load(os.path.join(MODELS_ENHANCED_DIR, 'rating_scaler.joblib'))
        text_length_scaler = joblib.load(os.path.join(MODELS_ENHANCED_DIR, 'text_length_scaler.joblib'))
        model_enhanced = joblib.load(os.path.join(MODELS_ENHANCED_DIR, 'random_forest_model.joblib'))

        with open(os.path.join(MODELS_ENHANCED_DIR, 'model_metadata.json'), 'r') as f:
            enhanced_metadata = json.load(f)

        ENHANCED_AVAILABLE = True
        print("✅ Modelo enhanced carregado!")
    except FileNotFoundError:
        ENHANCED_AVAILABLE = False
        print("⚠️ Modelo enhanced não encontrado. Usando apenas modelo original.")

    # Criar mapeamento reverso
    reverse_mapping = {v: k for k, v in sentiment_mapping.items()}

    print("✅ Modelos carregados com sucesso!")
    
    # Mostrar endpoints ativos
    print("\n" + "="*70)
    print("🚀 SENTIMENT ANALYSIS API - ENDPOINTS ATIVOS")
    print("="*70)
    print("\n📊 MODELOS DISPONÍVEIS:")
    print("   ✅ Original Model (TF-IDF + Logistic Regression)")
    if ENHANCED_AVAILABLE:
        print("   ✅ Enhanced Model (TF-IDF + Random Forest)")
    else:
        print("   ⚠️  Enhanced Model (não encontrado)")
    
    print("\n🔌 ENDPOINTS:")
    print("   POST /predict")
    print("        → Predição usando modelo original (texto apenas)")
    print("        → Request: {\"text\": \"seu texto aqui\"}")
    print("")
    print("   POST /predict/enhanced")
    print("        → Predição usando modelo enhanced")
    print("        → Request: {\"text\": \"...\", \"rating\": 1-5, \"recommend_to_friend\": true/false}")
    print("")
    print("   POST /predict/auto")
    print("        → Escolhe modelo automaticamente")
    print("        → Request: {\"text\": \"...\", \"rating\": 1-5 (opcional), \"recommend_to_friend\": true/false (opcional)}")
    print("")
    print("   GET /docs")
    print("        → Documentação interativa (Swagger UI)")
    print("        → Teste endpoints diretamente")
    print("")
    print("   GET /redoc")
    print("        → Documentação alternativa (ReDoc)")
    print("")
    print("   GET /health")
    print("        → Status da API")
    print("\n" + "="*70)
    print("API pronta! 🎯\n")
    
    yield
    
    # Shutdown (se necessário)
    print("🛑 Encerrando API...")

app = FastAPI(
    title="Enhanced Sentiment Analysis API",
    description="API para análise de sentimentos com suporte a múltiplas features",
    version="2.0.0",
    lifespan=lifespan
)

# ============================================================================
# MODELOS PYDANTIC
# ============================================================================

class SentimentRequestBasic(BaseModel):
    """Modelo para requisição básica (texto apenas)"""
    text: str = Field(..., min_length=3, max_length=10000, description="Texto para análise")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "text": "Este produto é excelente! Recomendo!"
            }
        }
    )

class SentimentRequestEnhanced(BaseModel):
    """Modelo para requisição enhanced (texto + rating + recomendação)"""
    text: str = Field(..., min_length=3, max_length=10000, description="Texto para análise")
    rating: int = Field(..., ge=1, le=5, description="Rating de 1-5 estrelas")
    recommend_to_friend: bool = Field(..., description="Se recomendaria a um amigo")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "text": "Este produto é excelente! Recomendo!",
                "rating": 5,
                "recommend_to_friend": True
            }
        }
    )

class SentimentRequestAuto(BaseModel):
    """Modelo para requisição auto (parâmetros opcionais)"""
    text: str = Field(..., min_length=3, max_length=10000, description="Texto para análise")
    rating: Optional[int] = Field(None, ge=1, le=5, description="Rating de 1-5 estrelas (opcional)")
    recommend_to_friend: Optional[bool] = Field(None, description="Se recomendaria a um amigo (opcional)")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "text": "Este produto é bom",
                "rating": 4,
                "recommend_to_friend": True
            }
        }
    )

class SentimentResponse(BaseModel):
    """Modelo para resposta de predição"""
    previsao: str = Field(..., description="Sentimento predito")
    probabilidade: float = Field(..., ge=0, le=1, description="Confiança da predição")
    probabilidades_detalhadas: Dict[str, float] = Field(..., description="Probabilidades para cada classe")
    modelo_usado: str = Field(..., description="Tipo de modelo usado")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "previsao": "Positivo",
                "probabilidade": 0.92,
                "probabilidades_detalhadas": {
                    "Negativo": 0.03,
                    "Neutro": 0.05,
                    "Positivo": 0.92
                },
                "modelo_usado": "enhanced"
            }
        }
    )

# ============================================================================
# FUNÇÕES AUXILIARES
# ============================================================================

# Funções de ajuste de bias removidas - correções devem ser feitas via retreinamento dos modelos

def predict_basic(text: str) -> Dict:
    """Predição usando modelo original (apenas texto)"""
    try:
        # Vetorizar
        text_tfidf = tfidf_original.transform([text])

        # Predizer
        pred_label = model_original.predict(text_tfidf)[0]
        pred_proba = model_original.predict_proba(text_tfidf)[0].copy()

        # Usar probabilidades diretas do modelo original
        sentiment = reverse_mapping[int(np.argmax(pred_proba))]
        confidence = float(np.max(pred_proba))

        probabilidades_detalhadas = {
            reverse_mapping[i]: float(pred_proba[i])
            for i in range(len(pred_proba))
        }

        return {
            "previsao": sentiment,
            "probabilidade": confidence,
            "probabilidades_detalhadas": probabilidades_detalhadas,
            "modelo_usado": "original"
        }

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro no modelo original: {str(e)}"
        )

def predict_enhanced(text: str, rating: int, recommend_to_friend: bool) -> Dict:
    """Predição usando modelo enhanced (múltiplas features)"""
    try:
        # 1. TF-IDF do texto
        text_tfidf = tfidf_enhanced.transform([text])

        # 2. Rating normalizado
        rating_scaled = rating_scaler.transform([[rating]])

        # 3. Recomendação como feature binária
        recommend_dummy = np.array([[1 if recommend_to_friend else 0]])

        # 4. Comprimento do texto normalizado
        text_length_scaled = text_length_scaler.transform([[len(text)]])

        # Combinar features
        X_combined = hstack([
            text_tfidf,
            rating_scaled,
            recommend_dummy,
            text_length_scaled
        ])

        # Usar probabilidades diretas do modelo enhanced
        enhanced_proba = model_enhanced.predict_proba(X_combined)[0].copy()

        # Ajustar baseado em rating (feature importante)
        if rating <= 2:
            # Rating baixo reforça negativo, mas não domina se texto for muito positivo
            enhanced_proba[0] *= 1.4  # Aumenta negativo moderadamente
            enhanced_proba[2] *= 0.7  # Reduz positivo moderadamente
        elif rating >= 4:
            # Rating alto reforça positivo, mas não domina se texto for muito negativo
            enhanced_proba[2] *= 1.3  # Aumenta positivo moderadamente
            enhanced_proba[0] *= 0.7  # Reduz negativo moderadamente
        
        # PRIORIDADE 3: Recomendação (peso menor, apenas complemento)
        if not recommend_to_friend:
            enhanced_proba[0] *= 1.2  # Leve aumento no negativo
            enhanced_proba[2] *= 0.9  # Leve redução no positivo

        # Renormalizar
        enhanced_proba = enhanced_proba / enhanced_proba.sum()

        pred_label = int(np.argmax(enhanced_proba))
        sentiment = reverse_mapping[pred_label]
        confidence = float(np.max(enhanced_proba))

        probabilidades_detalhadas = {
            reverse_mapping[i]: float(enhanced_proba[i])
            for i in range(len(enhanced_proba))
        }

        return {
            "previsao": sentiment,
            "probabilidade": confidence,
            "probabilidades_detalhadas": probabilidades_detalhadas,
            "modelo_usado": "enhanced_text_priority"
        }

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro no modelo enhanced: {str(e)}"
        )

# ============================================================================
# ENDPOINTS
# ============================================================================

@app.post("/predict", response_model=SentimentResponse, tags=["Predictions"])
async def predict_sentiment_basic(request: SentimentRequestBasic):
    """
    Análise de sentimento usando apenas texto (modelo original)

    **Parâmetros:**
    - text: String com o texto para análise (mínimo 3 caracteres)

    **Retorna:**
    - previsao: Sentimento (Positivo, Negativo ou Neutro)
    - probabilidade: Confiança da predição (0-1)
    - probabilidades_detalhadas: Probabilidades para cada sentimento
    - modelo_usado: Tipo de modelo usado
    """
    try:
        # Validação
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

        # Usar modelo original
        result = predict_basic(request.text.strip())
        return result

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro ao processar: {str(e)}"
        )

@app.post("/predict/enhanced", response_model=SentimentResponse, tags=["Predictions"])
async def predict_sentiment_enhanced(request: SentimentRequestEnhanced):
    """
    Análise de sentimento usando múltiplas features (modelo enhanced)

    **Parâmetros:**
    - text: String com o texto para análise
    - rating: Rating de 1-5 estrelas
    - recommend_to_friend: Se recomendaria a um amigo (true/false)

    **Retorna:**
    - previsao: Sentimento (Positivo, Negativo ou Neutro)
    - probabilidade: Confiança da predição (0-1)
    - probabilidades_detalhadas: Probabilidades para cada sentimento
    - modelo_usado: Tipo de modelo usado
    """
    try:
        # Verificar se modelo enhanced está disponível
        if not ENHANCED_AVAILABLE:
            raise HTTPException(
                status_code=503,
                detail="Modelo enhanced não está disponível. Use /predict para análise básica."
            )

        # Validação
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

        # Usar modelo enhanced
        result = predict_enhanced(
            request.text.strip(),
            request.rating,
            request.recommend_to_friend
        )
        return result

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro ao processar: {str(e)}"
        )

@app.post("/predict/auto", response_model=SentimentResponse, tags=["Predictions"])
async def predict_sentiment_auto(request: SentimentRequestAuto):
    """
    Análise automática - usa enhanced se parâmetros disponíveis, senão usa básico

    **Parâmetros:**
    - text: Texto para análise (obrigatório)
    - rating: Rating 1-5 (opcional)
    - recommend_to_friend: Recomendação (opcional)

    **Retorna:** Mesma estrutura dos outros endpoints
    """
    try:
        # Validação básica
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

        # Decidir qual modelo usar
        if ENHANCED_AVAILABLE and request.rating is not None and request.recommend_to_friend is not None:
            # Usar enhanced
            result = predict_enhanced(request.text.strip(), request.rating, request.recommend_to_friend)
        else:
            # Usar básico
            result = predict_basic(request.text.strip())

        return result

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Erro ao processar: {str(e)}"
        )

@app.get("/health", tags=["Health"])
async def health_check():
    """Verifica saúde da API e disponibilidade de modelos"""
    return {
        "status": "OK",
        "modelos_disponiveis": {
            "original": True,
            "enhanced": ENHANCED_AVAILABLE
        },
        "endpoints": {
            "predict": "/predict (texto apenas)",
            "predict/enhanced": "/predict/enhanced (múltiplas features)",
            "predict/auto": "/predict/auto (automático)"
        }
    }

@app.get("/", tags=["Info"])
async def root():
    """Informações da API Enhanced"""
    return {
        "titulo": "Enhanced Sentiment Analysis API",
        "versao": "2.0.0",
        "descricao": "API para análise de sentimentos com suporte a múltiplas features",
        "modelos_disponiveis": {
            "original": "Texto apenas (TF-IDF + Logistic Regression)",
            "enhanced": "Múltiplas features (TF-IDF + Rating + Recomendação + Random Forest)" if ENHANCED_AVAILABLE else "Não disponível"
        },
        "endpoints": {
            "health": "/health",
            "predict": "/predict",
            "predict/enhanced": "/predict/enhanced",
            "predict/auto": "/predict/auto",
            "docs": "/docs"
        }
    }

# ============================================================================
# INICIAR SERVIDOR
# ============================================================================

if __name__ == "__main__":
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=int(sys.argv[1]) if len(sys.argv) > 1 else 8000,
        log_level="info"
    )