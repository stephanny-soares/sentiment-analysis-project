from fastapi import APIRouter, status
from pydantic import BaseModel
from datetime import datetime
from typing import Dict

router = APIRouter()

class HealthResponse(BaseModel):
    status: str
    timestamp: datetime
    version: str

@router.get(
    "/health",
    response_model=HealthResponse,
    status_code=status.HTTP_200_OK,
    summary="Health Check",
    description="Verifica se a API está funcionando"
)
async def health_check() -> HealthResponse:
    """Verifica se a API está saudável"""
    return HealthResponse(
        status="healthy",
        timestamp=datetime.now(),
        version="1.0.0"
    )

@router.get("/ping", status_code=status.HTTP_200_OK)
async def ping() -> Dict[str, str]:
    """Simples ping"""
    return {"message": "pong"}
