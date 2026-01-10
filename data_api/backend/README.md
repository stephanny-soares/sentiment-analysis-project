# Backend - Java Spring Boot

## 🚀 Como rodar o servidor

### Via Docker Compose (recomendado)
Ver [README raiz](../README.md) para passo a passo completo do Docker.
```bash
# a partir da raiz do projeto
sudo docker-compose up -d
```
- Backend: http://localhost:8080

### Local (fora do Docker)
Pré-requisitos: Java 17+, Maven 3.6+
```bash
# terminal 1: subir Python API
cd data_science
python sentiment_api.py

# terminal 2: subir backend
cd backend
mvn spring-boot:run
```

Teste rápido:
```bash
curl -X POST "http://localhost:8080/api/sentiment/predict" \
   -H "Content-Type: application/json" \
   -d '{"text": "Este produto é excelente!"}'
```

## 📋 API Endpoints

### Information Endpoints
```bash
# API overview and status
curl http://localhost:8080/

# Detailed API information
curl http://localhost:8080/api
```

### Sentiment Analysis
```bash
curl -X POST "http://localhost:8080/api/sentiment/predict" \
     -H "Content-Type: application/json" \
     -d '{"text": "This product is amazing!"}'
```

**Response:**
```json
{
  "previsao": "Positivo",
  "probabilidade": 0.85,
  "mensagem": "Análise concluída com sucesso"
}
```

### Enhanced Sentiment Analysis (Recommended)

The Java backend now supports the Enhanced Model with multiple features for higher accuracy:

```bash
# Enhanced prediction with rating and recommendation
curl -X POST "http://localhost:8080/api/sentiment/predict/enhanced" \
     -H "Content-Type: application/json" \
     -d '{
       "text": "Este produto é excelente!",
       "rating": 5,
       "recommend_to_friend": true
     }'
```

**Enhanced Response:**
```json
{
  "previsao": "Positivo",
  "probabilidade": 0.92,
  "probabilidades_detalhadas": {
    "Negativo": 0.02,
    "Neutro": 0.06,
    "Positivo": 0.92
  },
  "modelo_usado": "enhanced"
}
```

### Auto Prediction (Smart Selection)

Automatically chooses the best available model:

```bash
# Uses Enhanced Model (all parameters provided)
curl -X POST "http://localhost:8080/api/sentiment/predict/auto?text=Amazing product!&rating=5&recommend_to_friend=true"

# Uses Original Model (only text provided)
curl -X POST "http://localhost:8080/api/sentiment/predict/auto?text=Amazing product!"
```

## 🏗️ Architecture

The Java backend acts as an API gateway that:
- Receives HTTP requests from clients
- Validates input data
- Calls the Python ML service internally
- Returns formatted responses
- Provides additional business logic layer

### Service Communication
- Java Backend (port 8080) → Python API (container: api, port 5000)
- Uses RestTemplate for HTTP communication
- Container networking via Docker Compose
- Supports both Original and Enhanced ML models

### Model Selection Logic
The backend intelligently routes requests to the appropriate model:

- **`/api/sentiment/predict`** → Always uses Original Model (text-only)
- **`/api/sentiment/predict/enhanced`** → Uses Enhanced Model (multi-feature)
- **`/api/sentiment/predict/auto`** → Auto-selects based on provided parameters:
  - All parameters (text + rating + recommendation) → Enhanced Model
  - Only text → Original Model
  - Enhanced model unavailable → Falls back to Original Model

### Available Models
- **Original Model**: TF-IDF + Logistic Regression (~88% accuracy)
- **Enhanced Model**: TF-IDF + Rating + Recommendation + Random Forest (~92% accuracy)

##  Key Components

- `SentimentController.java` - REST API endpoints
- `SentimentService.java` - Business logic and Python API communication
- `SentimentRequest.java` - Input validation DTO
- `SentimentResponse.java` - Response DTO
- `SentimentApiApplication.java` - Spring Boot main class

## ⚙️ Configuration

### Docker Environment
- Uses `http://api:5000` to communicate with Python service
- Runs on port 8080
- Profile: `docker`

### Local Environment
- Uses `http://localhost:5000` for Python service
- Runs on port 8080
- Default profile

## 🔧 Building

### Docker Build
```bash
sudo docker-compose build backend
```

### Maven Build
```bash
cd backend
mvn clean package
```

## 🐛 Troubleshooting

### Common Issues

1. **Connection refused to Python API**
   - Ensure Python service is running
   - Check container networking: `sudo docker-compose ps`

2. **Port conflicts**
   - Port 8080 might be in use by another service

3. **Build failures**
   - Ensure Java 17+ and Maven are installed
   - Check for compilation errors: `mvn compile`

### Logs
```bash
sudo docker-compose logs backend
```