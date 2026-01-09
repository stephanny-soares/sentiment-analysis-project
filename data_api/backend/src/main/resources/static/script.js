let selectedRating = 0;
let selectedRecommendation = null;
let selectedModel = 'auto';

// Wake up services on page load (Render free tier spin-down)
async function wakeUpServices() {
    try {
        // Visit Python API root to wake it up
        await fetch('https://sentiment-api-t3ng.onrender.com/', { 
            method: 'GET'
        }).catch(() => {});
        
        // Visit backend root to wake it up
        await fetch(window.location.origin + '/', { 
            method: 'GET'
        }).catch(() => {});
        
        console.log('Services wake-up initiated');
    } catch (error) {
        console.log('Wake-up completed');
    }
}

// Inicializar UI
document.addEventListener('DOMContentLoaded', function() {
    wakeUpServices();
    initializeStars();
    initializeTabs();
    updateSections();
    document.getElementById('textInput').focus();
});

function initializeStars() {
    const stars = document.querySelectorAll('.star');
    const ratingText = document.getElementById('ratingText');

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.dataset.rating);
            selectedRating = rating;
            updateStars(rating);
            ratingText.textContent = `Avaliação selecionada: ${rating} estrela${rating > 1 ? 's' : ''}`;
        });
    });
}

function updateStars(rating) {
    const stars = document.querySelectorAll('.star');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });
}

function initializeTabs() {
    const tabs = document.querySelectorAll('.model-tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            selectedModel = tab.dataset.model;
            updateSections();
        });
    });
}

function updateSections() {
    const ratingSection = document.getElementById('ratingSection');
    const recommendationSection = document.getElementById('recommendationSection');
    const modelNotice = document.getElementById('modelNotice');
    const ratingText = document.getElementById('ratingText');

    if (selectedModel === 'original') {
        ratingSection.style.display = 'none';
        recommendationSection.style.display = 'none';
        modelNotice.textContent = 'Modelo original: apenas texto é usado.';
    } else if (selectedModel === 'enhanced') {
        ratingSection.style.display = 'block';
        recommendationSection.style.display = 'block';
        modelNotice.textContent = 'Modelo enhanced: texto, rating e recomendação são obrigatórios.';
    } else {
        // auto
        ratingSection.style.display = 'block';
        recommendationSection.style.display = 'block';
        modelNotice.textContent = 'Modelo automático: só o texto é obrigatório; rating e recomendação são opcionais.';
    }

    // Reset selections when hiding sections
    if (selectedModel === 'original') {
        selectedRating = 0;
        updateStars(0);
        ratingText.textContent = 'Clique nas estrelas para avaliar (1-5)';
        selectedRecommendation = null;
        document.querySelectorAll('.recommendation-option').forEach(option => option.classList.remove('selected'));
        document.querySelectorAll('input[name="recommendation"]').forEach(r => r.checked = false);
    }
}

// Lidar com seleção de recomendação
document.querySelectorAll('input[name="recommendation"]').forEach(radio => {
    radio.addEventListener('change', function() {
        selectedRecommendation = this.value === 'true';
        // Atualizar visual
        document.querySelectorAll('.recommendation-option').forEach(option => {
            option.classList.remove('selected');
        });
        this.closest('.recommendation-option').classList.add('selected');
    });
});

async function analyzeSentiment() {
    const textInput = document.getElementById('textInput');
    const analyzeBtn = document.getElementById('analyzeBtn');
    const loading = document.getElementById('loading');
    const result = document.getElementById('result');

    const text = textInput.value.trim();

    if (!text) {
        showResult('Por favor, digite um texto para análise.', 'error');
        return;
    }

    if (text.length < 3) {
        showResult('O texto deve ter pelo menos 3 caracteres.', 'error');
        return;
    }

    // Validações específicas para modelo enhanced (todos obrigatórios)
    if (selectedModel === 'enhanced') {
        if (selectedRating === 0) {
            showResult('Para o modelo Enhanced, selecione uma avaliação (1-5 estrelas).', 'error');
            return;
        }
        if (selectedRecommendation === null) {
            showResult('Para o modelo Enhanced, indique se recomendaria a um amigo.', 'error');
            return;
        }
    }

    // Show loading state
    analyzeBtn.disabled = true;
    loading.style.display = 'block';
    result.style.display = 'none';

    try {
        // Preparar payload baseado no modelo selecionado
        let payload = { text: text };
        let endpoint = '/api/sentiment/predict';

        // Usar rating/recomendação conforme modelo
        if (selectedRating > 0 && selectedModel !== 'original') {
            payload.rating = selectedRating;
        }
        if (selectedRecommendation !== null && selectedModel !== 'original') {
            payload.recommendToFriend = selectedRecommendation;
        }

        const response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            showSuccessResult(data);
        } else {
            showResult(`Erro: ${data.message || 'Erro desconhecido'}`, 'error');
        }

    } catch (error) {
        console.error('Error:', error);
        showResult('Erro ao conectar com o servidor. Verifique se a API está rodando.', 'error');
    } finally {
        analyzeBtn.disabled = false;
        loading.style.display = 'none';
    }
}

function showSuccessResult(data) {
    const result = document.getElementById('result');

    // Determine sentiment class
    let sentimentClass = 'neutro';
    if (data.previsao.toLowerCase().includes('positivo')) {
        sentimentClass = 'positivo';
    } else if (data.previsao.toLowerCase().includes('negativo')) {
        sentimentClass = 'negativo';
    }

    // Informações sobre o modelo usado
    const modelInfo = data.modelo_usado ?
        `<div style="margin-top: 10px; font-size: 14px; color: #666;">
            <strong>Modelo usado:</strong> ${data.modelo_usado}
            ${data.modelo_usado === 'enhanced' ? ' 🚀' : data.modelo_usado === 'original' ? ' 📝' : ' 🤖'}
        </div>` : '';

    result.innerHTML = `
        <div class="sentiment-badge ${sentimentClass}">${data.previsao}</div>
        <div class="probability">
            Probabilidade: ${(data.probabilidade * 100).toFixed(1)}%
        </div>
        <div>
            <strong>Mensagem:</strong> ${data.mensagem}
        </div>
        ${modelInfo}
    `;

    result.className = 'result success';
    result.style.display = 'block';
}

function showResult(message, type) {
    const result = document.getElementById('result');
    result.textContent = message;
    result.className = `result ${type}`;
    result.style.display = 'block';
}

// Allow Enter key to submit
document.getElementById('textInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        analyzeSentiment();
    }
});

// Focus on textarea when page loads
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('textInput').focus();
});