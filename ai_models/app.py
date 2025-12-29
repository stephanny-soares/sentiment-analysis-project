import joblib

# Carregar o modelo
modelo = joblib.load('modelo_sentimental_final.pkl')

# Teste simples
teste = ["O produto chegou muito rápido e é ótimo"]
predicao = modelo.predict(teste)

print(f"Resultado do teste: {predicao}")