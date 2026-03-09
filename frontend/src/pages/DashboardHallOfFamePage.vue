<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">Hall da Fama</div>
        <div class="page-subtitle">
          Brabo do Mês e os campeões históricos da barbearia.
        </div>
      </div>
      <span class="tag">Dashboard • Hall da Fama</span>
    </div>

    <div class="grid-2">
      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Brabo do Mês atual</div>
        </div>
        <div v-if="!latest" class="page-subtitle">
          Nenhum registro no Hall da Fama ainda.
        </div>
        <div v-else style="display: flex; gap: 1.2rem; align-items: center">
          <img
            v-if="latest.photoUrl"
            :src="resolveBackendUrl(latest.photoUrl)"
            alt=""
            style="width: 120px; height: 120px; border-radius: 999px; object-fit: cover; border: 3px solid rgba(234,179,8,0.9)"
          />
          <div>
            <div style="font-size: 1.4rem; font-weight: 700">{{ latest.barberName }}</div>
            <div class="page-subtitle" style="margin: 0.25rem 0 0.75rem">
              {{ monthNames[latest.month - 1] }} / {{ latest.year }}
            </div>
            <div class="pill-row">
              <span class="pill">
                Desempenho:
                <strong>{{ formatPercent(latest.performancePercent) }}</strong>
              </span>
            </div>
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Histórico do Hall da Fama</div>
        </div>
        <div v-if="entries.length === 0" class="page-subtitle">
          O histórico aparecerá aqui quando os registros forem cadastrados.
        </div>
        <div v-else style="display: flex; flex-direction: column; gap: 0.5rem">
          <div
            v-for="entry in entries"
            :key="entry.id"
            class="leaderboard-row"
            style="border: 1px solid rgba(55,65,81,0.9)"
          >
            <div class="leaderboard-main">
              <img
                v-if="entry.photoUrl"
                :src="resolveBackendUrl(entry.photoUrl)"
                alt=""
                class="avatar-small"
              />
              <div>
                <div style="font-weight: 600">{{ entry.barberName }}</div>
                <div class="page-subtitle">
                  {{ monthNames[entry.month - 1] }} / {{ entry.year }}
                </div>
              </div>
            </div>
            <div class="percent-pill">
              {{ formatPercent(entry.performancePercent) }}
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { hallOfFameService } from '../services/api';
import { resolveBackendUrl } from '../services/api';

const latest = ref(null);
const entries = ref([]);
const intervalId = ref(null);

const monthNames = [
  'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
  'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'
];

function formatPercent(value) {
  if (value == null) return '-';
  return `${Number(value).toFixed(1)}%`;
}

async function loadData() {
  latest.value = await hallOfFameService.getLatest();
  entries.value = await hallOfFameService.list();
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

