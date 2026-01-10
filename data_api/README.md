# Sentiment Analysis Hackathon Project

## 🏁 Como rodar tudo com Docker (passo a passo)

1) **Pré-requisitos**
  - Docker e Docker Compose instalados
  - 4GB RAM e 8GB de disco livres

2) **Clonar o repositório**
  ```bash
  git clone https://github.com/kaio326/hackathon-nocountry-one-alura.git
  cd hackathon-nocountry-one-alura
  ```

3) **Subir todos os serviços**
  ```bash
  sudo docker-compose up -d
  ```
  Primeira execução leva alguns minutos para baixar imagens, instalar dependências Python, compilar o Spring Boot e preparar volumes.

4) **Verificar se está rodando**
  ```bash
  sudo docker-compose ps
  ```
  Todos os serviços devem aparecer como "Up".

5) **Testar no frontend**
  - Abra http://localhost:8080
  - Informe um texto e clique em "📊 Analisar Sentimento"

6) **Testes rápidos via curl**
  - Java API (texto):
  ```bash
  curl -X POST "http://localhost:8080/api/sentiment/predict" \
      -H "Content-Type: application/json" \
      -d '{"text": "Ótimo produto!"}'
  ```
  - Enhanced (texto+rating+recomendação):
  ```bash
  curl -X POST http://localhost:8000/api/v1/sentiment/predict/enhanced \
      -H "Content-Type: application/json" \
      -d '{"text":"Este produto é excelente!","rating":5,"recommend_to_friend":true}'
  ```

7) **Parar serviços**
  ```bash
  sudo docker-compose down
  ```

8) **Rebuild após mudanças de código**
  ```bash
  sudo docker-compose up --build -d
  ```

## 🎯 Project Overview

A complete sentiment analysis system with:
- **Machine Learning Model**: TF-IDF + Logistic Regression for sentiment classification
- **Java API Gateway**: Spring Boot backend providing REST endpoints and validation
- **Python ML Service**: FastAPI microservice handling AI predictions
- **Database**: PostgreSQL for data storage
- **Cache**: Redis for performance optimization
- **Containerization**: Docker Compose for easy deployment

## 🤖 AI Models

### Available Models

| Model | Features | Algorithm | Accuracy | Use Case |
|-------|----------|-----------|----------|----------|
| **Original** | Text only | TF-IDF + Logistic Regression | ~78% | Basic sentiment analysis |
| **Enhanced** | Text + Rating + Recommendation | TF-IDF + Random Forest | ~99% | Advanced analysis with metadata |

### Model Comparison

The **Enhanced Model** provides significantly better accuracy by incorporating:
- **Text Analysis**: TF-IDF vectorization of review content
- **Rating Score**: 1-5 star rating as numerical feature (26% importance)
- **Recommendation**: Binary feature (would recommend to friend) (30% importance)
- **Text Length**: Additional contextual information

**Expected Improvement**: 26% accuracy gain, especially for edge cases.

### Training Enhanced Model

```bash
# Navigate to notebooks directory
cd data_science/notebooks

# Run the enhanced model training
jupyter notebook enhanced_model_training.ipynb
```

This will create improved models in `data_science/models/enhanced/`

## � Security Configuration

### Environment Variables

**NEVER commit the `.env` file to version control!** It contains sensitive credentials.

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your secure credentials:**
   ```bash
   nano .env  # or your preferred editor
   ```

### Production Deployment

For production, use the secure configuration:

```bash
# Copy production environment file
cp .env.example .env.prod
# Edit with production credentials

# Deploy with production compose file
docker-compose -f docker-compose.prod.yml up -d
```

### Security Features Implemented

- ✅ **No hardcoded credentials** - All sensitive data uses environment variables
- ✅ **Redis authentication** - Redis requires password authentication
- ✅ **Restricted CORS** - Only allows specific origins
- ✅ **Input validation** - Text length limits and sanitization
- ✅ **Error handling** - Generic error messages in production
- ✅ **Resource limits** - Memory and CPU limits on containers
- ✅ **No privileged containers** - Security hardening applied
- ✅ **Secure headers** - HTTP security headers configured

### Security Best Practices

- 🔐 **Change default passwords** before deployment
- 🚫 **Never expose database ports** in production
- 🔒 **Use HTTPS** in production environments
- 📊 **Monitor logs** for suspicious activity
- 🔄 **Regular updates** of Docker images and dependencies

## 📋 Serviços e Portas

| Service | URL | Description |
|---------|-----|-------------|
| **🎨 Web Interface** | http://localhost:8080 | **Interactive sentiment analysis** (recommended) |
| **Java API** | http://localhost:8080 | **Main API endpoint** (Spring Boot) |
| **API Info** | http://localhost:8080/ | API overview and endpoints |
| **API Details** | http://localhost:8080/api | Detailed API information |
| **🚀 Enhanced API** | http://localhost:8000 | **Advanced sentiment analysis** (FastAPI) |
| **Enhanced API Docs** | http://localhost:8000/docs | Enhanced API documentation |
| Python API | http://localhost:5000 | Internal ML service (FastAPI) |
| API Documentation | http://localhost:5000/docs | Python API docs |
| pgAdmin | http://localhost:5050 | Database administration |
| PostgreSQL | localhost:5432 | Database server |
| Redis | localhost:6379 | Cache server |

## 🎨 Como usar o frontend (mais fácil)

Após rodar `sudo docker-compose up -d`:
1. Abra http://localhost:8080
2. Digite um texto e clique em "📊 Analisar Sentimento"

Detalhes de endpoints e APIs: veja [backend/README.md](backend/README.md) e [data_science/README.md](data_science/README.md).

## 🔧 Gerenciamento Docker

**Ver logs:**
```bash
sudo docker-compose logs -f [service-name]
```

**Restart:**
```bash
sudo docker-compose restart [service-name]
```

**Rebuild após mudanças:**
```bash
sudo docker-compose up --build -d
```

**Limpar:**
```bash
sudo docker-compose down -v
```

## 🔧 Troubleshooting

### Common Docker Issues

**Permission denied:**
```bash
# Use sudo for Docker commands
sudo docker-compose up -d
```

**Port already in use:**
```bash
sudo lsof -i :8080
# Stop conflicting services or change ports in docker-compose.yml
```

**Build fails:**
```bash
sudo docker system prune -a
sudo docker-compose build --no-cache
```

**Services not starting:**
```bash
sudo docker-compose ps
sudo docker-compose logs [service-name]
```

**Out of disk space:**
```bash
df -h
sudo docker system prune -a --volumes
```

## 📁 Project Structure

```
├── data_science/          # ML models and training code
│   ├── models/           # Trained models and vectorizers
│   ├── datasets/         # Training data
│   ├── notebooks/        # Jupyter notebooks for training
│   └── sentiment_api.py  # FastAPI microservice
├── backend/              # Java Spring Boot API gateway
│   ├── src/              # Java source code
│   ├── resources/        # Application properties
│   ├── Dockerfile        # Java container configuration
│   └── pom.xml           # Maven dependencies
├── docker-compose.yml    # Container orchestration
├── Dockerfile           # Python API container configuration
└── requirements.txt     # Python dependencies
```

## � Documentação dos módulos

- [Backend Java (Spring Boot)](backend/README.md) - Endpoints e execução local
- [Data Science (Python ML)](data_science/README.md) - Modelos, notebooks, APIs Python

## 🛠️ Development Setup (opcional)

Para rodar componentes individualmente fora do Docker:
- Python: veja [data_science/README.md](data_science/README.md)
- Java: veja [backend/README.md](backend/README.md)

## 📊 Model Performance

- **Accuracy**: ~88%
- **Negative Recall**: 96% (optimized for negative comment detection)
- **Classes**: Positive, Neutral, Negative
- **Features**: TF-IDF with 1000 features, bigrams included

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test with Docker
5. Submit a pull request

## 📄 License

This project is part of the 2025 Oracle/Alura/NoCountry Hackathon.
