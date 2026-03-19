<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">Progresso Mensal</div>
        <div class="page-subtitle">
          Acompanhe em tempo real o progresso de cada barbeiro em relação à meta mensal. Atualizado a cada 10 segundos.
        </div>
      </div>
      <span class="tag">Dashboard • Progresso Mensal</span>
    </div>

    <div class="grid-2">
      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Tabela de progresso</div>
          <span class="chip">Meta do mês exibida como 100%</span>
        </div>
        <table class="table">
          <thead>
            <tr>
              <th>Barbeiro</th>
              <th>Meta mensal</th>
              <th>Progresso atual</th>
              <th>Restante</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in progress" :key="row.barberId">
              <td>
                <div style="display: flex; align-items: center; gap: 0.5rem">
                  <img
                    v-if="row.photoUrl"
                    :src="resolveBackendUrl(row.photoUrl)"
                    alt=""
                    class="avatar-small"
                  />
                  <span>{{ row.barberName }}</span>
                </div>
              </td>
              <td>100%</td>
              <td>
                <div style="display: flex; flex-direction: column; gap: 0.25rem">
                  <div class="progress-bar">
                    <div
                      class="progress-bar-fill"
                      :class="barClass(row.weeklyProgressPercent)"
                      :style="{ width: clampPercent(row.weeklyProgressPercent) + '%' }"
                    ></div>
                  </div>
                  <span style="font-size: 0.75rem; color: #d1d5db">
                    {{ formatPercent(row.weeklyProgressPercent) }}
                  </span>
                </div>
              </td>
              <td>
                {{ formatPercent(row.remainingPercent) }}
              </td>
            </tr>
          </tbody>
        </table>
      </section>

      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Gráfico de progresso</div>
          <span class="chip">Conclusão relativa por barbeiro (mês)</span>
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

const progress = ref([]);
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

function clampPercent(value) {
  const n = Number(value ?? 0);
  if (n < 0) return 0;
  if (n > 140) return 140;
  return n;
}

function barClass(value) {
  const n = Number(value ?? 0);
  if (n >= 100) return '';
  if (n >= 70) return 'warning';
  return 'danger';
}

async function loadData() {
  const data = await dashboardService.getMonthlyProgress();
  const sortedData = [...data].sort(
    (a, b) => Number(b.weeklyProgressPercent ?? 0) - Number(a.weeklyProgressPercent ?? 0)
  );
  progress.value = sortedData;

  chartData.value = {
    labels: sortedData.map((d) => d.barberName),
    datasets: [
      {
        label: 'Progresso mensal',
        data: sortedData.map((d) => Number(d.weeklyProgressPercent ?? 0)),
        backgroundColor: 'rgba(34,197,94,0.8)',
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
