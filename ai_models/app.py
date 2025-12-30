from flask import Flask, request, jsonify
import joblib

app = Flask(__name__)

# 1. Carregar os modelos uma única vez quando o servidor sobe
try:
    modelo_sentimento = joblib.load('modelo_sentimental_final.pkl')
    modelo_atraso = joblib.load('modelo_atraso_final.pkl')
    print("✅ Modelos carregados com sucesso!")
except Exception as e:
    print(f"❌ Erro ao carregar modelos: {e}")

# 2. Rota para análise de sentimento
@app.route('/predict/sentiment', methods=['POST'])
def predict_sentiment():
    dados = request.get_json()
    texto = dados.get('texto', '')
    
    if not texto:
        return jsonify({'erro': 'Texto não fornecido'}), 400
    
    # O modelo espera uma lista [texto]
    predicao = modelo_sentimento.predict([texto])
    
    # Retorna o resultado (ex: 1, 2, 3, 4 ou 5)
    return jsonify({'sentimento': int(predicao[0])})

# 3. Rota para análise de atraso
@app.route('/predict/delay', methods=['POST'])
def predict_delay():
    dados = request.get_json()
    texto = dados.get('texto', '')
    
    if not texto:
        return jsonify({'erro': 'Texto não fornecido'}), 400
    
    predicao = modelo_atraso.predict([texto])
    
    # Retorna 1 para atraso e 0 para no prazo
    return jsonify({'atraso': int(predicao[0])})

if __name__ == '__main__':
    # O servidor rodará na porta 5000
    app.run(port=5000, debug=True)