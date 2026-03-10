<template>
  <div class="tv-root">
    <div class="tv-screen">
      <div class="tv-inner">
        <header class="tv-header">
          <div>
            <div class="tv-title">Game Cantareli Barber</div>
            <div class="page-subtitle">
              Metas semanais, ranking ao vivo e o Brabo do Mês.
            </div>
          </div>
          <div class="tv-mode-pill">
            Modo TV •
            <span v-if="currentView === 'progress'">Progresso Semanal</span>
            <span v-else-if="currentView === 'ranking'">Ranking Semanal</span>
            <span v-else-if="currentView === 'rankingHistory'">Histórico Semanal</span>
            <span v-else-if="currentView === 'progressMonth'">Progresso Mensal</span>
            <span v-else-if="currentView === 'rankingMonth'">Ranking Mensal</span>
            <span v-else-if="currentView === 'brabo'">Brabo do Mês</span>
            <span v-else>Hall da Fama</span>
          </div>
        </header>

        <section v-if="currentView === 'progress'" class="tv-grid">
          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Progresso Semanal</div>
              <div class="tv-panel-subtitle">Cada barbeiro em busca de 100%+ esta semana.</div>
            </div>
            <div v-if="progress.length === 0" class="page-subtitle">
              Aguardando dados de performance desta semana.
            </div>
            <table v-else class="table">
              <thead>
                <tr>
                  <th>Barbeiro</th>
                  <th>Progresso</th>
                  <th>Restante</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in progress" :key="row.barberId">
                  <td>
                    <div style="display: flex; align-items: center; gap: 0.6rem">
                      <img
                        v-if="row.photoUrl"
                        :src="resolveBackendUrl(row.photoUrl)"
                        alt=""
                        class="avatar-small"
                      />
                      <span style="font-size: 1rem">{{ row.barberName }}</span>
                    </div>
                  </td>
                  <td>
                    <div class="progress-bar" style="height: 0.85rem">
                      <div
                        class="progress-bar-fill"
                        :class="barClass(row.weeklyProgressPercent)"
                        :style="{ width: clampPercent(row.weeklyProgressPercent) + '%' }"
                      ></div>
                    </div>
                    <div style="font-size: 0.8rem; margin-top: 0.15rem">
                      {{ formatPercent(row.weeklyProgressPercent) }}
                    </div>
                  </td>
                  <td style="font-size: 0.9rem">
                    {{ formatPercent(row.remainingPercent) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Barras de progresso</div>
              <div class="tv-panel-subtitle">Gráfico de progresso horizontal.</div>
            </div>
            <div class="chart-container">
              <Bar v-if="progressChart" :data="progressChart" :options="progressChartOptions" />
            </div>
          </div>
        </section>

        <section v-else-if="currentView === 'ranking'" class="tv-grid">
          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Ranking Semanal</div>
              <div class="tv-panel-subtitle">Quem está liderando esta semana?</div>
            </div>
            <div v-if="ranking.length === 0" class="page-subtitle">
              O ranking aparece quando os valores de performance forem registrados.
            </div>
            <div v-else style="display: flex; flex-direction: column; gap: 0.7rem">
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
                    <div style="font-size: 1rem; font-weight: 600">
                      {{ row.barberName }}
                    </div>
                    <div class="page-subtitle">Conclusão semanal</div>
                  </div>
                </div>
                <div class="percent-pill" style="font-size: 0.95rem">
                  {{ formatPercent(row.weeklyProgressPercent) }}
                </div>
              </div>
            </div>
          </div>

          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Barras do ranking</div>
              <div class="tv-panel-subtitle">Gráfico de barras de conclusão %.</div>
            </div>
            <div class="chart-container">
              <Bar v-if="rankingChart" :data="rankingChart" :options="rankingChartOptions" />
            </div>
          </div>
        </section>

        <section v-else-if="currentView === 'rankingHistory'" class="tv-history-section">
          <div class="tv-panel-header tv-history-header">
            <div class="tv-panel-title">Histórico Semanal</div>
            <div class="tv-panel-subtitle">Rankings das semanas anteriores</div>
          </div>
          <div v-if="weeksWithRanking.length === 0" class="page-subtitle">
            Nenhuma semana anterior com lançamentos.
          </div>
          <div v-else class="tv-history-grid">
            <div
              v-for="item in weeksWithRanking"
              :key="item.week.weekStart"
              class="tv-history-week"
            >
              <div class="tv-history-week-header">{{ item.week.label }}</div>
              <div class="tv-history-week-list">
                <div
                  v-for="row in item.ranking"
                  :key="`${item.week.weekStart}-${row.barberId}`"
                  class="leaderboard-row tv-history-row"
                  :style="leaderboardStyle(row.position)"
                >
                  <div class="leaderboard-main">
                    <div class="position-pill tv-history-pill" :class="positionClass(row.position)">
                      {{ medalFor(row.position) }} {{ row.position }}
                    </div>
                    <img
                      v-if="row.photoUrl"
                      :src="resolveBackendUrl(row.photoUrl)"
                      alt=""
                      class="tv-history-avatar"
                    />
                    <div class="tv-history-name">{{ row.barberName }}</div>
                  </div>
                  <div class="percent-pill tv-history-percent">
                    {{ formatPercent(row.weeklyProgressPercent) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section v-else-if="currentView === 'progressMonth'" class="tv-grid">
          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Progresso Mensal</div>
              <div class="tv-panel-subtitle">Cada barbeiro em busca de 100%+ este mês.</div>
            </div>
            <div v-if="progressMonth.length === 0" class="page-subtitle">
              Aguardando dados de performance deste mês.
            </div>
            <table v-else class="table">
              <thead>
                <tr>
                  <th>Barbeiro</th>
                  <th>Progresso</th>
                  <th>Restante</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in progressMonth" :key="row.barberId">
                  <td>
                    <div style="display: flex; align-items: center; gap: 0.6rem">
                      <img
                        v-if="row.photoUrl"
                        :src="resolveBackendUrl(row.photoUrl)"
                        alt=""
                        class="avatar-small"
                      />
                      <span style="font-size: 1rem">{{ row.barberName }}</span>
                    </div>
                  </td>
                  <td>
                    <div class="progress-bar" style="height: 0.85rem">
                      <div
                        class="progress-bar-fill"
                        :class="barClass(row.weeklyProgressPercent)"
                        :style="{ width: clampPercent(row.weeklyProgressPercent) + '%' }"
                      ></div>
                    </div>
                    <div style="font-size: 0.8rem; margin-top: 0.15rem">
                      {{ formatPercent(row.weeklyProgressPercent) }}
                    </div>
                  </td>
                  <td style="font-size: 0.9rem">
                    {{ formatPercent(row.remainingPercent) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Barras de progresso (mês)</div>
            </div>
            <div class="chart-container">
              <Bar v-if="progressMonthChart" :data="progressMonthChart" :options="progressChartOptions" />
            </div>
          </div>
        </section>

        <section v-else-if="currentView === 'rankingMonth'" class="tv-grid">
          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Ranking Mensal</div>
              <div class="tv-panel-subtitle">Quem está liderando este mês?</div>
            </div>
            <div v-if="rankingMonth.length === 0" class="page-subtitle">
              O ranking aparece quando os valores de performance forem registrados.
            </div>
            <div v-else style="display: flex; flex-direction: column; gap: 0.7rem">
              <div
                v-for="row in rankingMonth"
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
                    <div style="font-size: 1rem; font-weight: 600">{{ row.barberName }}</div>
                    <div class="page-subtitle">Conclusão mensal</div>
                  </div>
                </div>
                <div class="percent-pill" style="font-size: 0.95rem">
                  {{ formatPercent(row.weeklyProgressPercent) }}
                </div>
              </div>
            </div>
          </div>
          <div class="tv-panel">
            <div class="tv-panel-header">
              <div class="tv-panel-title">Barras do ranking (mês)</div>
            </div>
            <div class="chart-container">
              <Bar v-if="rankingMonthChart" :data="rankingMonthChart" :options="rankingChartOptions" />
            </div>
          </div>
        </section>

        <section v-else-if="currentView === 'brabo'" class="tv-grid">
          <div class="tv-panel tv-panel-brabo" style="grid-column: span 2; align-items: center; text-align: center">
            <div class="tv-panel-header" style="justify-content: center">
              <div class="tv-panel-title">Brabo do Mês</div>
            </div>
            <div v-if="!latestHof" class="page-subtitle">
              Nenhum registro no Hall da Fama ainda.
            </div>
            <div
              v-else
              class="tv-brabo-content"
            >
              <img
                v-if="latestHof.photoUrl"
                :src="resolveBackendUrl(latestHof.photoUrl)"
                alt=""
                class="tv-brabo-photo"
              />
              <div class="tv-brabo-name">{{ latestHof.barberName }}</div>
              <div class="page-subtitle tv-brabo-period">
                {{ monthNames[latestHof.month - 1] }} / {{ latestHof.year }}
              </div>
              <div class="pill-row" style="justify-content: center">
                <span class="pill tv-brabo-pill">
                  Desempenho:
                  <strong>{{ formatPercent(latestHof.performancePercent) }}</strong>
                </span>
              </div>
            </div>
          </div>
        </section>

        <section v-else-if="currentView === 'hallOfFame'" class="tv-grid">
          <div class="tv-panel tv-panel-hall" style="grid-column: span 2">
            <div class="tv-panel-header tv-panel-header-stacked" style="justify-content: center">
              <div class="tv-panel-title">Hall da Fama</div>
              <div class="tv-panel-subtitle">Todos os destaques do mês</div>
            </div>
            <div v-if="!hofHistory.length" class="page-subtitle">
              Nenhum registro no Hall da Fama ainda.
            </div>
            <div v-else class="tv-hof-grid">
              <div
                v-for="entry in hofHistory"
                :key="entry.id"
                class="tv-hof-card"
              >
                <img
                  v-if="entry.photoUrl"
                  :src="resolveBackendUrl(entry.photoUrl)"
                  alt=""
                  class="tv-hof-card-photo"
                />
                <div class="tv-hof-card-name">{{ entry.barberName }}</div>
                <div class="tv-hof-card-period">
                  {{ monthNames[entry.month - 1] }} / {{ entry.year }}
                </div>
                <div class="tv-hof-card-percent">
                  {{ formatPercent(entry.performancePercent) }}
                </div>
              </div>
            </div>
          </div>
        </section>

        <footer class="tv-footer">
          <span>Rotação automática a cada 10 segundos</span>
          <span>Última atualização: {{ lastUpdated }}</span>
        </footer>
      </div>
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
import { dashboardService, hallOfFameService } from '../services/api';
import { resolveBackendUrl } from '../services/api';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const currentView = ref('progress'); // progress | ranking | rankingHistory | progressMonth | rankingMonth | brabo | hallOfFame
const progress = ref([]);
const ranking = ref([]);
const weeksWithRanking = ref([]);
const progressMonth = ref([]);
const rankingMonth = ref([]);
const latestHof = ref(null);
const hofHistory = ref([]);
const lastUpdated = ref('-');

const progressChart = ref(null);
const rankingChart = ref(null);
const progressMonthChart = ref(null);
const rankingMonthChart = ref(null);

const rotationIntervalId = ref(null);
const refreshIntervalId = ref(null);

const monthNames = [
  'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
  'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'
];

const baseChartOptions = {
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
        color: '#e5e7eb',
        font: {
          size: 12
        }
      },
      grid: {
        display: false
      }
    }
  }
};

const progressChartOptions = baseChartOptions;
const rankingChartOptions = baseChartOptions;

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
  const [
    progressData,
    rankingData,
    progressMonthData,
    rankingMonthData,
    latestHofData,
    hofListData,
    availableWeeks
  ] = await Promise.all([
    dashboardService.getWeeklyProgress(),
    dashboardService.getWeeklyRanking(),
    dashboardService.getMonthlyProgress(),
    dashboardService.getMonthlyRanking(),
    hallOfFameService.getLatest(),
    hallOfFameService.list(),
    dashboardService.getAvailableWeeks(8)
  ]);

  progress.value = progressData;
  ranking.value = rankingData;
  progressMonth.value = progressMonthData;

  const historyItems = await Promise.all(
    availableWeeks.map(async (week) => {
      const ranking = await dashboardService.getWeeklyRankingForWeek(week.weekStart);
      return { week, ranking };
    })
  );
  weeksWithRanking.value = historyItems;
  rankingMonth.value = rankingMonthData;
  latestHof.value = latestHofData;
  hofHistory.value = hofListData;

  progressChart.value = {
    labels: progressData.map((d) => d.barberName),
    datasets: [
      {
        label: 'Progresso semanal',
        data: progressData.map((d) => Number(d.weeklyProgressPercent ?? 0)),
        backgroundColor: 'rgba(34,197,94,0.85)',
        borderRadius: 16,
        borderSkipped: false
      }
    ]
  };

  rankingChart.value = {
    labels: rankingData.map((d) => `${d.position}. ${d.barberName}`),
    datasets: [
      {
        label: 'Conclusão semanal',
        data: rankingData.map((d) => Number(d.weeklyProgressPercent ?? 0)),
        backgroundColor: rankingData.map((d) => {
          if (d.position === 1) return 'rgba(250,204,21,0.95)';
          if (d.position === 2) return 'rgba(148,163,184,0.95)';
          if (d.position === 3) return 'rgba(248,113,113,0.95)';
          return 'rgba(34,197,94,0.85)';
        }),
        borderRadius: 16,
        borderSkipped: false
      }
    ]
  };

  progressMonthChart.value = {
    labels: progressMonthData.map((d) => d.barberName),
    datasets: [
      {
        label: 'Progresso mensal',
        data: progressMonthData.map((d) => Number(d.weeklyProgressPercent ?? 0)),
        backgroundColor: 'rgba(34,197,94,0.85)',
        borderRadius: 16,
        borderSkipped: false
      }
    ]
  };

  rankingMonthChart.value = {
    labels: rankingMonthData.map((d) => `${d.position}. ${d.barberName}`),
    datasets: [
      {
        label: 'Conclusão mensal',
        data: rankingMonthData.map((d) => Number(d.weeklyProgressPercent ?? 0)),
        backgroundColor: rankingMonthData.map((d) => {
          if (d.position === 1) return 'rgba(250,204,21,0.95)';
          if (d.position === 2) return 'rgba(148,163,184,0.95)';
          if (d.position === 3) return 'rgba(248,113,113,0.95)';
          return 'rgba(34,197,94,0.85)';
        }),
        borderRadius: 16,
        borderSkipped: false
      }
    ]
  };

  const now = new Date();
  lastUpdated.value = now.toLocaleTimeString('pt-BR');
}

function startRotation() {
  rotationIntervalId.value = setInterval(() => {
    if (currentView.value === 'progress') {
      currentView.value = 'ranking';
    } else if (currentView.value === 'ranking') {
      currentView.value = 'rankingHistory';
    } else if (currentView.value === 'rankingHistory') {
      currentView.value = 'progressMonth';
    } else if (currentView.value === 'progressMonth') {
      currentView.value = 'rankingMonth';
    } else if (currentView.value === 'rankingMonth') {
      currentView.value = 'brabo';
    } else if (currentView.value === 'brabo') {
      currentView.value = 'hallOfFame';
    } else {
      currentView.value = 'progress';
    }
  }, 10000);
}

function startRefresh() {
  refreshIntervalId.value = setInterval(loadData, 10000);
}

onMounted(async () => {
  await loadData();
  startRotation();
  startRefresh();
});

onBeforeUnmount(() => {
  if (rotationIntervalId.value) clearInterval(rotationIntervalId.value);
  if (refreshIntervalId.value) clearInterval(refreshIntervalId.value);
});
</script>

