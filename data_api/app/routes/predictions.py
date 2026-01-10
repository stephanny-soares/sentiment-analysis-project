from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
import logging
import httpx
import os

logger = logging.getLogger(__name__)
router = APIRouter()

# URL da API de ML (ajuste conforme necessário)
ML_API_URL = os.getenv("ML_API_URL", "http://localhost:5000")

class SentimentInput(BaseModel):
    text: str = Field(..., min_length=1, max_length=5000, description="Texto para análise de sentimento")

    class Config:
        json_schema_extra = {
            "example": {
                "text": "Este produto é excelente!"
            }
        }

class SentimentInputEnhanced(BaseModel):
    text: str = Field(..., min_length=1, max_length=5000, description="Texto para análise de sentimento")
    rating: int = Field(..., ge=1, le=5, description="Rating de 1 a 5 estrelas")
    recommend_to_friend: bool = Field(..., description="Recomendaria para um amigo")

    class Config:
        json_schema_extra = {
            "example": {
                "text": "Este produto é excelente!",
                "rating": 5,
                "recommend_to_friend": True
            }
        }

class SentimentOutput(BaseModel):
    previsao: str = Field(..., description="Sentimento predito (Positivo/Negativo/Neutro)")
    probabilidade: float = Field(..., ge=0.0, le=1.0, description="Probabilidade da predição")
    mensagem: str = Field(..., description="Mensagem de status")
    modelo_usado: Optional[str] = Field(None, description="Modelo usado na predição")

    class Config:
        json_schema_extra = {
            "example": {
                "previsao": "Positivo",
                "probabilidade": 0.92,
                "mensagem": "Análise concluída com sucesso",
                "modelo_usado": "enhanced"
            }
        }

class PredictionInput(BaseModel):
    features: List[float] = Field(..., description="Features para predição")
    model_version: Optional[str] = Field("1.0.0")

    class Config:
        json_schema_extra = {
            "example": {
                "features": [1.5, 2.3, 3.1, 4.2],
                "model_version": "1.0.0"
            }
        }


class SentimentInputAuto(BaseModel):
    text: str = Field(..., min_length=1, max_length=5000)
    rating: Optional[int] = Field(None, ge=1, le=5)
    recommend_to_friend: Optional[bool] = None

class PredictionOutput(BaseModel):
    prediction: float = Field(..., description="Valor predito")
    confidence: float = Field(..., description="Confiança da predição (0-1)")
    model_version: str = Field(..., description="Versão do modelo")

async def call_ml_api(endpoint: str, payload: Dict[str, Any]) -> Dict[str, Any]:
    """Chama a API de ML de forma assíncrona"""
    async with httpx.AsyncClient(timeout=30.0) as client:
        try:
            response = await client.post(f"{ML_API_URL}{endpoint}", json=payload)
            response.raise_for_status()
            return response.json()
        except httpx.HTTPError as e:
            logger.error(f"Erro na chamada para API ML: {e}")
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail="Serviço de ML indisponível. Tente novamente mais tarde."
            )

@router.post(
    "/sentiment/predict",
    response_model=SentimentOutput,
    status_code=status.HTTP_200_OK,
    summary="Análise de Sentimento (Modelo Original)",
    description="Analisa sentimento usando apenas o texto (modelo original)"
)
async def predict_sentiment(input_data: SentimentInput) -> SentimentOutput:
    """Analisa sentimento usando o modelo original (texto apenas)"""
    try:
        logger.info(f"Analisando sentimento para texto de {len(input_data.text)} caracteres")

        # Chamar API de ML
        ml_response = await call_ml_api("/predict", {"text": input_data.text})

        return SentimentOutput(
            previsao=ml_response.get("previsao", "Erro"),
            probabilidade=ml_response.get("probabilidade", 0.0),
            mensagem=ml_response.get("mensagem", "Análise concluída"),
            modelo_usado="original"
        )

    except Exception as e:
        logger.error(f"Erro na análise de sentimento: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Erro ao processar análise de sentimento"
        )

@router.post(
    "/sentiment/predict/enhanced",
    response_model=SentimentOutput,
    status_code=status.HTTP_200_OK,
    summary="Análise de Sentimento Enhanced",
    description="Analisa sentimento usando texto + rating + recomendação (modelo aprimorado)"
)
async def predict_sentiment_enhanced(input_data: SentimentInputEnhanced) -> SentimentOutput:
    """Analisa sentimento usando o modelo enhanced (múltiplas features)"""
    try:
        logger.info(f"Analisando sentimento enhanced - Rating: {input_data.rating}, Recomendação: {input_data.recommend_to_friend}")

        # Chamar API de ML com endpoint enhanced
        payload = {
            "text": input_data.text,
            "rating": input_data.rating,
            "recommend_to_friend": input_data.recommend_to_friend
        }
        ml_response = await call_ml_api("/predict/enhanced", payload)

        return SentimentOutput(
            previsao=ml_response.get("previsao", "Erro"),
            probabilidade=ml_response.get("probabilidade", 0.0),
            mensagem=ml_response.get("mensagem", "Análise concluída"),
            modelo_usado="enhanced"
        )

    except Exception as e:
        logger.error(f"Erro na análise enhanced: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Erro ao processar análise de sentimento enhanced"
        )

@router.post(
    "/sentiment/predict/auto",
    response_model=SentimentOutput,
    status_code=status.HTTP_200_OK,
    summary="Análise Inteligente (Auto-seleção)",
    description="Escolhe automaticamente o melhor modelo baseado nos dados fornecidos"
)
async def predict_sentiment_auto(input_data: SentimentInputAuto) -> SentimentOutput:
    """Escolhe automaticamente o modelo baseado nos parâmetros fornecidos"""
    try:
        has_rating = input_data.rating is not None
        has_recommendation = input_data.recommend_to_friend is not None

        if has_rating and has_recommendation:
            # Usar modelo enhanced
            logger.info("Usando modelo enhanced (dados completos)")
            enhanced_input = SentimentInputEnhanced(**input_data.model_dump())
            return await predict_sentiment_enhanced(enhanced_input)
        else:
            # Usar modelo original
            logger.info("Usando modelo original (dados limitados)")
            original_input = SentimentInput(text=input_data.text)
            return await predict_sentiment_original(original_input)

    except Exception as e:
        logger.error(f"Erro na análise auto: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Payload inválido para análise automática"
        )

@router.post(
    "/predict",
    response_model=PredictionOutput,
    status_code=status.HTTP_200_OK,
    summary="Fazer Predição (Genérica)",
    description="Endpoint genérico para predições (mantido para compatibilidade)"
)
async def predict(input_data: PredictionInput) -> PredictionOutput:
    """Faz uma predição genérica com features fornecidas (para compatibilidade)"""
    try:
        # TODO: Implementar predição genérica se necessário
        prediction = sum(input_data.features) / len(input_data.features)

        return PredictionOutput(
            prediction=prediction,
            confidence=0.95,
            model_version=input_data.model_version
        )
    except Exception as e:
        logger.error(f"Erro na predição: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Erro ao processar predição"
        )

@router.post(
    "/batch-predict",
    response_model=List[PredictionOutput],
    summary="Predições em Lote"
)
async def batch_predict(inputs: List[PredictionInput]) -> List[PredictionOutput]:
    """Faz múltiplas predições de uma vez"""
    results = []
    for input_data in inputs:
        result = await predict(input_data)
        results.append(result)
    return results
