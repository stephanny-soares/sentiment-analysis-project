const API_BASE = "http://localhost:8082/sentiment";
let chart;
let currentRating = null;
let currentRecommend = null;

function setRating(rating) {
  currentRating = rating;
  const stars = document.querySelectorAll('.star-btn');
  stars.forEach((star, index) => {
    if (index < rating) {
      star.textContent = '★';
      star.style.color = '#facc15'; // yellow
    } else {
      star.textContent = '☆';
      star.style.color = '';
    }
  });
  updateModelInfo();
}

function clearRating() {
  currentRating = null;
  const stars = document.querySelectorAll('.star-btn');
  stars.forEach(star => {
    star.textContent = '☆';
    star.style.color = '';
  });
  updateModelInfo();
}

function setRecommend(value) {
  currentRecommend = value;
  const thumbUp = document.getElementById('thumbUp');
  const thumbDown = document.getElementById('thumbDown');
  
  if (value === true) {
    thumbUp.style.opacity = '1';
    thumbUp.style.transform = 'scale(1.1)';
    thumbDown.style.opacity = '0.4';
    thumbDown.style.transform = 'scale(1)';
  } else {
    thumbDown.style.opacity = '1';
    thumbDown.style.transform = 'scale(1.1)';
    thumbUp.style.opacity = '0.4';
    thumbUp.style.transform = 'scale(1)';
  }
  updateModelInfo();
}

function clearRecommend() {
  currentRecommend = null;
  const thumbUp = document.getElementById('thumbUp');
  const thumbDown = document.getElementById('thumbDown');
  thumbUp.style.opacity = '0.4';
  thumbDown.style.opacity = '0.4';
  thumbUp.style.transform = 'scale(1)';
  thumbDown.style.transform = 'scale(1)';
  updateModelInfo();
}

function updateModelInfo() {
  const modelInfo = document.getElementById('modelInfo');
  const useEnhanced = currentRating !== null && currentRecommend !== null;
  modelInfo.innerText = useEnhanced ? 'Modelo: Enhanced (IA Avançada)' : 'Modelo: Básico';
}

function toggleDarkMode() {
  document.documentElement.classList.toggle('dark');
  const isDark = document.documentElement.classList.contains('dark');
  document.getElementById('themeIcon').innerText = isDark ? '☀️' : '🌙';
  atualizarDashboard();
}

async function analisar() {
  const textInput = document.getElementById('textInput');
  const modelInfo = document.getElementById('modelInfo');
  
  const text = textInput.value.trim();
  const rating = currentRating;
  const recommend = currentRecommend;
  
  const resCard = document.getElementById('resultCard');
  if (text.length < 4) return alert("Texto muito curto!");

  // Determinar qual modelo será usado
  const useEnhanced = rating !== null && recommend !== null;
  modelInfo.innerText = useEnhanced ? 'Modelo: Enhanced (IA Avançada)' : 'Modelo: Básico';

  try {
    // Construir payload
    const payload = { text: text };
    if (rating !== null) payload.rating = rating;
    if (recommend !== null) payload.recommendToFriend = recommend;

    const response = await fetch(API_BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    const data = await response.json();

    if (response.ok) {
      resCard.classList.remove('hidden');
      resCard.className = 'mt-8 p-6 rounded-2xl border-2 transition-all';

      const previsao = data.prediction;
      let style = 'bg-yellow-50 border-yellow-200 text-yellow-700 dark:bg-yellow-900/30 dark:border-yellow-700 dark:text-yellow-400';
      if (previsao === 'POSITIVO') style = 'bg-green-50 border-green-200 text-green-700 dark:bg-green-900/30 dark:border-green-700 dark:text-green-400';
      if (previsao === 'NEGATIVO') style = 'bg-red-50 border-red-200 text-red-700 dark:bg-red-900/30 dark:border-red-700 dark:text-red-400';

      resCard.classList.add(...style.split(' '));
      if (data.prioridade === 'CRÍTICA') resCard.classList.add('critical-pulse');

      document.getElementById('predictionText').innerText = previsao;
      document.getElementById('priorityText').innerText = data.prioridade;
      document.getElementById('sectorBadge').innerText = "SETOR: " + data.setor;
      document.getElementById('confidenceText').innerText = "Confiança: " + (data.confidence * 100).toFixed(1) + '%';
      document.getElementById('suggestionText').innerText = `"${data.sugestaoResposta}"`;

      const tagsContainer = document.getElementById('tagsContainer');
      tagsContainer.innerHTML = (data.tags || []).map(tag =>
        `<span class="bg-black/10 dark:bg-white/10 px-2 py-1 rounded text-[10px] font-black">#${tag.toUpperCase()}</span>`
      ).join('') || '<span class="text-[10px] opacity-40">Sem tags</span>';

      adicionarAoHistorico(text, previsao, data.setor, data.prioridade);
      atualizarDashboard();
      textInput.value = '';
      clearRating();
      clearRecommend();
      modelInfo.innerText = 'Modelo: Básico';
    }
  } catch (err) { alert("Erro de conexão!"); }
}

function adicionarAoHistorico(texto, previsao, setor, prioridade) {
  const table = document.getElementById('historyBody');
  let colors = {
    dot: "bg-yellow-400",
    text: "text-yellow-600 dark:text-yellow-400",
    bg: "bg-yellow-50/50 dark:bg-yellow-900/10"
  };

  if (previsao === 'POSITIVO') {
    colors = { dot: "bg-green-500", text: "text-green-600 dark:text-green-400", bg: "bg-green-50/50 dark:bg-green-900/10" };
  } else if (previsao === 'NEGATIVO') {
    colors = { dot: "bg-red-500", text: "text-red-600 dark:text-red-400", bg: "bg-red-50/50 dark:bg-red-900/10" };
  }

  const prioStyle = prioridade === 'CRÍTICA' ? 'bg-red-600 text-white' : 'bg-slate-200 dark:bg-slate-700 text-slate-600 dark:text-slate-300';

  const row = `
      <tr class="border-b border-slate-100 dark:border-slate-800 ${colors.bg} transition-all hover:bg-white dark:hover:bg-slate-800">
          <td class="py-4 px-4 rounded-l-xl">
              <div class="flex items-center gap-3">
                  <div class="w-2.5 h-2.5 rounded-full ${colors.dot}"></div>
                  <div>
                      <span class="text-[9px] font-black px-2 py-0.5 rounded bg-white/80 dark:bg-slate-700 text-slate-500 dark:text-slate-400 mb-1 inline-block uppercase tracking-tighter">${setor}</span>
                      <p class="text-slate-700 dark:text-slate-200 font-medium line-clamp-1 max-w-md">${texto}</p>
                  </div>
              </div>
          </td>
          <td class="py-4 px-4 text-center">
              <span class="px-2 py-1 rounded text-[10px] font-black ${prioStyle}">${prioridade}</span>
          </td>
          <td class="py-4 px-4 text-right rounded-r-xl">
              <span class="font-black italic tracking-tighter ${colors.text}">${previsao}</span>
          </td>
      </tr>
  `;
  table.innerHTML = row + table.innerHTML;
}

async function atualizarDashboard() {
  try {
    const res = await fetch(`${API_BASE}/stats`);
    const stats = await res.json();
    if (!stats || stats.length === 0) return;

    const labels = stats.map(s => {
      return (s.tipo || s.label || s.nome || Object.values(s).find(v => typeof v === 'string') || "N/A").toUpperCase();
    });
    const values = stats.map(s => {
      return s.quantidade || s.total || s.valor || Object.values(s).find(v => typeof v === 'number') || 0;
    });

    const isDark = document.documentElement.classList.contains('dark');
    if (chart) chart.destroy();
    const ctx = document.getElementById('sentimentChart').getContext('2d');
    chart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: values,
          backgroundColor: ['#22c55e', '#facc15', '#ef4444', '#6366f1'],
          borderWidth: 0
        }]
      },
      options: {
        maintainAspectRatio: false,
        cutout: '80%',
        plugins: {
          legend: {
            display: true,
            position: 'bottom',
            labels: {
              usePointStyle: true,
              color: isDark ? '#f8fafc' : '#475569',
              font: { weight: 'bold', size: 11 },
              padding: 20
            }
          }
        }
      }
    });
  } catch (err) { console.error("Erro no gráfico:", err); }
}

window.onload = atualizarDashboard;

function limparDados() {
    if (confirm("⚠️ ATENÇÃO: Isso apagará todas as análises do banco de dados. Deseja continuar?")) {
        fetch(`${API_BASE}/clear`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                alert("Erro ao tentar limpar os dados no servidor.");
            }
        })
        .catch(error => {
            console.error("Erro na requisição:", error);
            alert("Não foi possível conectar ao servidor para limpar os dados.");
        });
    }
}