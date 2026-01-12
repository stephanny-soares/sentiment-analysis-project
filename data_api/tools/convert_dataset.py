"""
Script para converter B2W-Reviews01 de CSV para JSON
Detecta automaticamente o delimitador correto
"""

import pandas as pd
import json
import os
from pathlib import Path
import csv

def detect_delimiter(file_path, sample_lines=5):
    """
    Detecta automaticamente qual é o delimitador do CSV
    
    Args:
        file_path (str): Caminho do arquivo CSV
        sample_lines (int): Número de linhas para analisar
    
    Returns:
        str: O delimitador detectado
    """
    with open(file_path, 'r', encoding='utf-8') as f:
        sample = ''.join([f.readline() for _ in range(sample_lines)])
    
    # Tentar detectar com Sniffer do csv
    try:
        delimiter = csv.Sniffer().sniff(sample).delimiter
        print(f"✅ Delimitador detectado: '{delimiter}'")
        return delimiter
    except:
        # Fallback: tentar delimitadores comuns
        for delim in [',', ';', '\t', '|']:
            with open(file_path, 'r', encoding='utf-8') as f:
                try:
                    test_df = pd.read_csv(f, sep=delim, nrows=5)
                    if len(test_df.columns) > 1:
                        print(f"✅ Delimitador detectado: '{delim}'")
                        return delim
                except:
                    continue
        
        print("⚠️  Não consegui detectar o delimitador automaticamente")
        return ','


def convert_csv_to_json(csv_file_path, json_output_path=None, delimiter=None):
    """
    Converte arquivo CSV do B2W-Reviews01 para JSON
    
    Args:
        csv_file_path (str): Caminho do arquivo CSV baixado
        json_output_path (str): Caminho onde salvar o JSON (opcional)
        delimiter (str): Delimitador do CSV (detectado automaticamente se None)
    
    Returns:
        dict: Informações sobre a conversão
    """
    
    print("=" * 60)
    print("🔄 INICIANDO CONVERSÃO CSV → JSON")
    print("=" * 60)
    
    # Validar se arquivo existe
    if not os.path.exists(csv_file_path):
        raise FileNotFoundError(f"❌ Arquivo não encontrado: {csv_file_path}")
    
    print(f"\n📁 Lendo arquivo: {csv_file_path}")
    print(f"   Tamanho: {os.path.getsize(csv_file_path) / (1024**2):.2f} MB")
    
    # Detectar delimitador se não fornecido
    if delimiter is None:
        print(f"\n🔍 Detectando delimitador...")
        delimiter = detect_delimiter(csv_file_path)
    else:
        print(f"\n📌 Usando delimitador fornecido: '{delimiter}'")
    
    try:
        # Ler CSV com configurações robustas
        df = pd.read_csv(
            csv_file_path,
            sep=delimiter,
            encoding='utf-8',
            on_bad_lines='skip',  # Pular linhas com problemas
            engine='python'  # Motor mais tolerante
        )
        
        print(f"\n✅ Arquivo carregado com sucesso!")
        print(f"   Total de linhas: {len(df)}")
        print(f"   Total de colunas: {len(df.columns)}")
        print(f"\n📋 Colunas encontradas:")
        for i, col in enumerate(df.columns, 1):
            print(f"   {i}. {col}")
        
    except Exception as e:
        print(f"\n❌ Erro ao ler CSV: {e}")
        print(f"\n💡 Dica: Verifique se o arquivo não está corrompido")
        raise
    
    # Definir caminho de saída se não fornecido
    if json_output_path is None:
        json_output_path = csv_file_path.replace('.csv', '.json')
    
    # Criar diretório se não existir
    output_dir = os.path.dirname(json_output_path)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)
        print(f"\n📁 Diretório criado: {output_dir}")
    
    print(f"\n💾 Convertendo para JSON...")
    
    try:
        # Converter para JSON com configurações apropriadas
        df.to_json(
            json_output_path,
            orient='records',           # Formato: lista de objetos
            force_ascii=False,          # Preservar acentos/caracteres especiais
            indent=2,                   # Indentar para legibilidade
            default_handler=str         # Converter tipos não-JSON para string
        )
        
        print(f"✅ JSON criado com sucesso!")
        print(f"   Salvo em: {json_output_path}")
        print(f"   Tamanho: {os.path.getsize(json_output_path) / (1024**2):.2f} MB")
        
        # Informações adicionais
        print(f"\n📊 RESUMO DA CONVERSÃO:")
        print(f"   Total de registros: {len(df)}")
        print(f"   Arquivo original (CSV): {os.path.getsize(csv_file_path) / (1024**2):.2f} MB")
        print(f"   Arquivo novo (JSON): {os.path.getsize(json_output_path) / (1024**2):.2f} MB")
        
        # Mostrar amostra do JSON gerado
        print(f"\n🔍 AMOSTRA DO JSON GERADO (primeiro registro):")
        print("-" * 60)
        with open(json_output_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
            if data:
                print(json.dumps(data[0], ensure_ascii=False, indent=2))
        print("-" * 60)
        
        return {
            'status': 'sucesso',
            'csv_original': csv_file_path,
            'json_novo': json_output_path,
            'total_registros': len(df),
            'colunas': df.columns.tolist(),
            'delimiter': delimiter
        }
    
    except Exception as e:
        print(f"❌ Erro ao converter para JSON: {e}")
        raise


def convert_csv_to_json_simple(csv_path, output_path, delimiter=None, sentiment_mapping=False):
    """
    Versão simplificada: apenas converte, opcionalmente adiciona coluna 'sentiment'
    
    Args:
        csv_path (str): Caminho do CSV
        output_path (str): Caminho de saída do JSON
        delimiter (str): Delimitador (detectado se None)
        sentiment_mapping (bool): Se True, converte rating em sentiment
    """
    
    if delimiter is None:
        delimiter = detect_delimiter(csv_path)
    
    df = pd.read_csv(csv_path, sep=delimiter, encoding='utf-8', on_bad_lines='skip', engine='python')
    
    # Se pedido, mapear ratings para sentimentos
    if sentiment_mapping and 'rating' in df.columns:
        def map_rating_to_sentiment(rating):
            if rating >= 4:
                return 'Positivo'
            elif rating <= 2:
                return 'Negativo'
            else:
                return 'Neutro'
        
        df['sentiment'] = df['rating'].apply(map_rating_to_sentiment)
        print(f"✅ Coluna 'sentiment' adicionada!")
        print(f"   Distribuição:\n{df['sentiment'].value_counts()}")
    
    df.to_json(output_path, orient='records', force_ascii=False, indent=2)
    print(f"✅ JSON salvo em: {output_path}")


# ============================================================================
# EXEMPLO DE USO
# ============================================================================

if __name__ == "__main__":
    
    # OPÇÃO 1: Conversão simples (apenas CSV → JSON)
    print("\n" + "=" * 60)
    print("OPÇÃO 1: CONVERSÃO SIMPLES")
    print("=" * 60)

    csv_file = "../datasets/B2W-Reviews01.csv"  # Mudar para seu arquivo se tiver nome diferente
    json_file = "../datasets/reviews.json"
    
    if os.path.exists(csv_file):
        resultado = convert_csv_to_json(csv_file, json_file)
        print(f"\n✅ Status: {resultado['status']}")
        print(f"   Saída: {resultado['json_novo']}")
        print(f"   Delimitador usado: '{resultado['delimiter']}'")
    else:
        print(f"\n⚠️  Arquivo '{csv_file}' não encontrado no diretório atual.")
        print(f"   Arquivos CSV disponíveis:")
        for f in Path('.').glob('*.csv'):
            print(f"      - {f}")
    
    # OPÇÃO 2: Conversão com mapeamento de sentimentos (opcional)
    print("\n\n" + "=" * 60)
    print("OPÇÃO 2: CONVERSÃO + MAPEAMENTO DE SENTIMENTOS")
    print("=" * 60)
    
    # Descomente para usar:
    # convert_csv_to_json_simple(
    #     csv_path="B2W-Reviews01.csv",
    #     output_path="reviews_with_sentiment.json",
    #     sentiment_mapping=True
    # )
    
    print("\n💡 Para usar a OPÇÃO 2, descomente as 5 últimas linhas do script!")
    
    print("\n" + "=" * 60)
    print("✨ Conversão concluída! Você pode agora usar o JSON no seu modelo.")
    print("=" * 60)
