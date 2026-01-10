#!/bin/bash

echo "🎯 Testando Interface Melhorada com Modelo Enhanced"
echo "=================================================="

# Teste 1: Modelo Original (apenas texto)
echo -e "\n📝 Teste 1: Modelo Original (Java API - apenas texto)"
curl -s -X POST http://localhost:8080/api/sentiment/predict \
  -H "Content-Type: application/json" \
  -d '{"text": "Este produto é excelente!"}'

# Teste 2: Modelo Enhanced (Java API - texto + rating + recomendação)
echo -e "\n🚀 Teste 2: Modelo Enhanced (Java API - texto + rating + recomendação)"
curl -s -X POST http://localhost:8080/api/sentiment/predict/enhanced \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Este produto é excelente!",
    "rating": 5,
    "recommend_to_friend": true
  }'

# Teste 3: Auto-seleção (Java API - dados completos → Enhanced)
echo -e "\n🤖 Teste 3: Auto-seleção (Java API - dados completos → Enhanced)"
curl -s -X POST http://localhost:8080/api/sentiment/predict/auto \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Este produto é excelente!",
    "rating": 5,
    "recommend_to_friend": true
  }'

# Teste 4: Auto-seleção (Java API - apenas texto → Original)
echo -e "\n📊 Teste 4: Auto-seleção (Java API - apenas texto → Original)"
curl -s -X POST http://localhost:8080/api/sentiment/predict/auto \
  -H "Content-Type: application/json" \
  -d '{"text": "Este produto é excelente!"}'

# Teste 5: Frontend API (porta 8000) - Enhanced
echo -e "\n🎨 Teste 5: Frontend API (porta 8000) - Enhanced"
curl -s -X POST http://localhost:8000/api/v1/sentiment/predict/enhanced \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Este produto é excelente!",
    "rating": 5,
    "recommend_to_friend": true
  }'

# Teste 6: Frontend API (porta 8000) - Auto
echo -e "\n🎭 Teste 6: Frontend API (porta 8000) - Auto-seleção"
curl -s -X POST http://localhost:8000/api/v1/sentiment/predict/auto \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Este produto é excelente!",
    "rating": 5,
    "recommend_to_friend": true
  }'

echo -e "\n✅ Testes concluídos!"
echo "📊 Compare as probabilidades: Enhanced deve ser mais preciso!"
echo "🌐 Interface web melhorada: http://localhost:8080"
echo "📚 API Docs Frontend: http://localhost:8000/docs"
