from fastapi import FastAPI
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging

# Importar rotas
from routes import health, predictions, training
from config import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("🚀 Iniciando aplicação...")
    yield
    logger.info("🛑 Encerrando aplicação...")

app = FastAPI(
    title=settings.API_TITLE,
    description="API para processar dados e fazer predições com ML",
    version=settings.API_VERSION,
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
    lifespan=lifespan
)

# CORS
allowed_origins = settings.ALLOWED_ORIGINS
allow_credentials = bool(allowed_origins and "*" not in allowed_origins)

app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=allow_credentials,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Rotas
app.include_router(health.router, prefix="/api/v1", tags=["Health"])
app.include_router(predictions.router, prefix="/api/v1", tags=["Predictions"])
app.include_router(training.router, prefix="/api/v1", tags=["Training"])

@app.get("/", include_in_schema=False)
async def root():
    return JSONResponse({
        "message": "Bem-vindo ao Hackathon Data Science API",
        "docs": "/docs",
        "redoc": "/redoc"
    })

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
