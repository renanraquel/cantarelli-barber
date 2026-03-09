<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">Ranking Semanal</div>
        <div class="page-subtitle">
          Visão competitiva dos barbeiros ordenados pela conclusão da meta semanal.
        </div>
      </div>
      <span class="tag">Dashboard • Ranking</span>
    </div>

    <div class="grid-2">
      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Classificação</div>
          <span class="chip">Atualizado a cada 10 segundos</span>
        </div>
        <div v-if="ranking.length === 0" class="page-subtitle">
          Nenhum dado para esta semana ainda.
        </div>
        <div v-else style="display: flex; flex-direction: column; gap: 0.5rem">
          <div
            v-for="row in ranking"
            :key="row.barberId"
            class="leaderboard-row"
            :style="leaderboardStyle(row.position)"
          >
            <div class="leaderboard-main">
              <div class="position-pill" :class="positionClass(row.position)">
                {{ medalFor(row.position) }} {{ row.position }}
              </div>
              <img
                v-if="row.photoUrl"
                :src="resolveBackendUrl(row.photoUrl)"
                alt=""
                class="avatar-small"
              />
              <div>
                <div style="font-weight: 600">{{ row.barberName }}</div>
                <div class="page-subtitle">Conclusão semanal</div>
              </div>
            </div>
            <div class="percent-pill">
              {{ formatPercent(row.weeklyProgressPercent) }}
            </div>
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Barras do ranking</div>
          <span class="chip">Gráfico de barras horizontal</span>
        </div>
        <div class="chart-container">
          <Bar v-if="chartData" :data="chartData" :options="chartOptions" />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { Bar } from 'vue-chartjs';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';
import { dashboardService } from '../services/api';
import { resolveBackendUrl } from '../services/api';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const ranking = ref([]);
const chartData = ref(null);
const intervalId = ref(null);

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label(ctx) {
          return `${ctx.parsed.x.toFixed(1)}%`;
        }
      }
    }
  },
  indexAxis: 'y',
  scales: {
    x: {
      min: 0,
      max: 140,
      ticks: {
        color: '#9ca3af',
        callback(value) {
          return `${value}%`;
        }
      },
      grid: {
        color: 'rgba(31,41,55,0.7)'
      }
    },
    y: {
      ticks: {
        color: '#e5e7eb'
      },
      grid: {
        display: false
      }
    }
  }
};

function formatPercent(value) {
  if (value == null) return '-';
  return `${Number(value).toFixed(1)}%`;
}

function medalFor(position) {
  if (position === 1) return '🥇';
  if (position === 2) return '🥈';
  if (position === 3) return '🥉';
  return '#';
}

function positionClass(position) {
  if (position === 1) return 'position-1';
  if (position === 2) return 'position-2';
  if (position === 3) return 'position-3';
  return '';
}

function leaderboardStyle(position) {
  if (position === 1) {
    return {
      background:
        'linear-gradient(90deg, rgba(250,204,21,0.18), rgba(15,23,42,0.95))',
      borderColor: 'rgba(250,204,21,0.7)'
    };
  }
  if (position === 2) {
    return {
      background:
        'linear-gradient(90deg, rgba(148,163,184,0.18), rgba(15,23,42,0.95))',
      borderColor: 'rgba(148,163,184,0.7)'
    };
  }
  if (position === 3) {
    return {
      background:
        'linear-gradient(90deg, rgba(249,115,22,0.18), rgba(15,23,42,0.95))',
      borderColor: 'rgba(249,115,22,0.7)'
    };
  }
  return {};
}

async function loadData() {
  const data = await dashboardService.getWeeklyRanking();
  ranking.value = data;

  chartData.value = {
    labels: data.map((d) => `${d.position}. ${d.barberName}`),
    datasets: [
      {
        label: 'Conclusão semanal',
        data: data.map((d) => Number(d.weeklyProgressPercent ?? 0)),
        backgroundColor: data.map((d) => {
          if (d.position === 1) return 'rgba(250,204,21,0.9)';
          if (d.position === 2) return 'rgba(148,163,184,0.9)';
          if (d.position === 3) return 'rgba(248,113,113,0.9)';
          return 'rgba(34,197,94,0.8)';
        }),
        borderRadius: 12,
        borderSkipped: false
      }
    ]
  };
}

onMounted(async () => {
  await loadData();
  intervalId.value = setInterval(loadData, 10000);
});

onBeforeUnmount(() => {
  if (intervalId.value) {
    clearInterval(intervalId.value);
  }
});
</script>

