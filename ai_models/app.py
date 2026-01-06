from flask import Flask, request, jsonify
import joblib
import re

app = Flask(__name__)

try:
    modelo_sentimento = joblib.load('logistic_regression_model.joblib')
    vectorizer = joblib.load('tfidf_vectorizer.joblib')
    
    if not hasattr(modelo_sentimento, 'multi_class'):
        modelo_sentimento.multi_class = 'auto' 
    # ----------------------------------
    
    print("✅ Novos modelos e Vetorizador carregados com sucesso!")
except Exception as e:
    print(f"❌ Erro ao carregar modelos: {e}")

def limpar_texto(texto):
    """Limpeza básica que modelos de Data Science costumam exigir"""
    texto = texto.lower() # Tudo em minúsculo
    texto = re.sub(r'[^\w\s]', '', texto) # Remove pontuação (!?.,)
    return texto.strip()

# 2. Rota para análise de sentimento
@app.route('/predict/sentiment', methods=['POST'])
def predict_sentiment():
    dados = request.get_json()
    texto_original = dados.get('texto', '')
    
    if not texto_original:
        return jsonify({'erro': 'Texto não fornecido'}), 400
    
    try:
        # Preparação do dado
        texto_pronto = limpar_texto(texto_original)
        texto_vetorizado = vectorizer.transform([texto_pronto])
        
        # Predição e Probabilidade (para debug)
        predicao = modelo_sentimento.predict(texto_vetorizado)[0]
        probabilidades = modelo_sentimento.predict_proba(texto_vetorizado)[0]
        
        # LOGS NO TERMINAL 
        print(f"\n--- NOVA ANÁLISE ---")
        print(f"Original: {texto_original}")
        print(f"Limpo: {texto_pronto}")
        print(f"Número do Modelo: {predicao}")
        print(f"Certezas (Probabs): {probabilidades}")
        print(f"--------------------\n")
        
        # Retorna o valor numérico para o Java
        return jsonify({
            'sentimento': int(predicao),
            'detalhes': {
                'confianca_por_classe': list(probabilidades),
                'texto_processado': texto_pronto
            }
        })
        
    except Exception as e:
        print(f"❌ Erro no processamento: {e}")
        return jsonify({'erro': str(e)}), 500

# 3. Rota para análise de atraso 
@app.route('/predict/delay', methods=['POST'])
def predict_delay():
    return jsonify({'atraso': 0, 'status': 'Funcionalidade em migração'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)