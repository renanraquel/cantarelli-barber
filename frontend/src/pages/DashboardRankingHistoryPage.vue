<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">Histórico Semanal</div>
        <div class="page-subtitle">
          Ranking das semanas anteriores (semana atual excluída).
        </div>
      </div>
      <span class="tag">Dashboard • Ranking Histórico</span>
    </div>

    <div v-if="loading" class="page-subtitle">Carregando...</div>
    <div v-else-if="weeksWithRanking.length === 0" class="page-subtitle">
      Nenhuma semana anterior com lançamentos encontrada.
    </div>

    <div v-else class="history-wrap">
      <div class="history-stack">
      <section
        v-for="item in weeksWithRanking"
        :key="item.week.weekStart"
        class="history-week-panel"
      >
        <div class="history-week-header">{{ item.week.label }}</div>
        <div class="history-week-list">
          <div
            v-for="row in item.ranking"
            :key="`${item.week.weekStart}-${row.barberId}`"
            class="leaderboard-row history-row"
            :style="leaderboardStyle(row.position)"
          >
            <div class="leaderboard-main">
              <div class="position-pill history-pill" :class="positionClass(row.position)">
                {{ medalFor(row.position) }} {{ row.position }}
              </div>
              <img
                v-if="row.photoUrl"
                :src="resolveBackendUrl(row.photoUrl)"
                alt=""
                class="avatar-history"
              />
              <div class="history-name">{{ row.barberName }}</div>
            </div>
            <div class="percent-pill history-percent">
              {{ formatPercent(row.weeklyProgressPercent) }}
            </div>
          </div>
        </div>
      </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { dashboardService } from '../services/api';
import { resolveBackendUrl } from '../services/api';

const weeksWithRanking = ref([]);
const loading = ref(false);

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

async function loadAllHistory() {
  loading.value = true;
  try {
    const weeks = await dashboardService.getAvailableWeeks(8);
    const items = await Promise.all(
      weeks.map(async (week) => {
        const ranking = await dashboardService.getWeeklyRankingForWeek(week.weekStart);
        return { week, ranking };
      })
    );
    weeksWithRanking.value = items;
  } finally {
    loading.value = false;
  }
}

onMounted(loadAllHistory);
</script>

<style scoped>
.history-wrap {
  min-height: 200px;
}

.history-stack {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  grid-auto-rows: min-content;
  grid-auto-flow: row;
  gap: 0.6rem;
}

.history-week-panel {
  background: rgba(31, 41, 55, 0.6);
  border: 1px solid rgba(55, 65, 81, 0.8);
  border-radius: 0.75rem;
  padding: 0.5rem 0.6rem;
  display: flex;
  flex-direction: column;
}

.history-week-list {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.history-week-header {
  font-weight: 600;
  font-size: 0.85rem;
  color: #86efac;
  margin-bottom: 0.4rem;
  padding-bottom: 0.3rem;
  border-bottom: 1px solid rgba(75, 85, 99, 0.6);
  flex-shrink: 0;
}

.history-row {
  font-size: 0.8rem;
  padding: 0.2rem 0.15rem;
}

.history-row .leaderboard-main {
  gap: 0.4rem;
}

.avatar-history {
  width: 22px;
  height: 22px;
  border-radius: 999px;
  object-fit: cover;
  border: 1px solid rgba(34, 197, 94, 0.6);
  flex-shrink: 0;
}

.history-name {
  font-weight: 600;
  font-size: 0.8rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
}

.history-pill {
  min-width: 28px;
  padding: 0.1rem 0.25rem;
  font-size: 0.7rem;
}

.history-percent {
  min-width: 44px;
  font-size: 0.8rem;
}
</style>
