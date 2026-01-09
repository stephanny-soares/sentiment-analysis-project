import os
from pydantic_settings import BaseSettings
from typing import List

class Settings(BaseSettings):
    API_TITLE: str = "Hackathon Data Science API"
    API_VERSION: str = "1.0.0"
    DATABASE_URL: str
    REDIS_URL: str | None = None
    REDIS_PASSWORD: str | None = None
    ENVIRONMENT: str = "development"
    DEBUG: bool = False
    ALLOWED_ORIGINS: List[str] = ["http://localhost:8080", "http://127.0.0.1:8080"]
    
    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()

# Build Redis URL securely if not provided directly
if not settings.REDIS_URL:
    redis_password = settings.REDIS_PASSWORD or os.getenv("REDIS_PASSWORD", "")
    password_part = f":{redis_password}@" if redis_password else ""
    settings.REDIS_URL = f"redis://{password_part}redis:6379/0"
