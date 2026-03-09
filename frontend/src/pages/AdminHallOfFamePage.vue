<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">Admin Hall da Fama</div>
        <div class="page-subtitle">Cadastre o barbeiro campeão do mês (Brabo do Mês).</div>
      </div>
      <span class="tag">Admin • Hall da Fama</span>
    </div>

    <div class="grid-2">
      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">{{ form.id ? 'Editar Brabo do Mês' : 'Cadastrar Brabo do Mês' }}</div>
          <button v-if="form.id" type="button" class="btn-ghost" @click="cancelEdit">
            Novo
          </button>
        </div>
        <form class="form-grid" @submit.prevent="submit">
          <div class="form-field">
            <label>Barbeiro</label>
            <select v-model.number="form.barberId" required>
              <option value="" disabled>Selecione o barbeiro</option>
              <option v-for="barber in barbers" :key="barber.id" :value="barber.id">
                {{ barber.name }}
              </option>
            </select>
          </div>
          <div class="form-field">
            <label>Mês</label>
            <select v-model.number="form.month" required>
              <option v-for="month in 12" :key="month" :value="month">
                {{ month }} - {{ monthNames[month - 1] }}
              </option>
            </select>
          </div>
          <div class="form-field">
            <label>Ano</label>
            <input v-model.number="form.year" type="number" min="2020" required />
          </div>
          <div class="form-field">
            <label>Performance (%)</label>
            <input v-model.number="form.performancePercent" type="number" min="0" step="0.01" required />
          </div>
          <div class="form-field" style="align-self: flex-end">
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ form.id ? 'Atualizar' : 'Salvar' }}
            </button>
          </div>
        </form>
      </section>

      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Hall da Fama recente</div>
        </div>
        <div v-if="entries.length === 0" class="page-subtitle">
          Nenhum registro ainda. Cadastre o primeiro Brabo do Mês.
        </div>
        <div v-else style="display: flex; flex-direction: column; gap: 0.75rem">
          <div
            v-for="entry in entries"
            :key="entry.id"
            class="leaderboard-row"
            style="border: 1px solid rgba(55, 65, 81, 0.9)"
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
            <div style="display: flex; align-items: center; gap: 0.5rem">
              <span class="percent-pill">{{ formatPercent(entry.performancePercent) }}</span>
              <button type="button" class="btn-ghost" @click="editEntry(entry)">
                Editar
              </button>
              <button type="button" class="btn-ghost btn-danger" @click="removeEntry(entry)">
                Remover
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { barberService, hallOfFameService } from '../services/api';
import { resolveBackendUrl } from '../services/api';

const barbers = ref([]);
const entries = ref([]);
const submitting = ref(false);

const monthNames = [
  'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
  'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'
];

const now = new Date();

const form = reactive({
  id: null,
  barberId: '',
  month: now.getMonth() + 1,
  year: now.getFullYear(),
  performancePercent: 100
});

function formatPercent(value) {
  if (value == null) return '-';
  return `${Number(value).toFixed(1)}%`;
}

function cancelEdit() {
  const d = new Date();
  form.id = null;
  form.barberId = '';
  form.month = d.getMonth() + 1;
  form.year = d.getFullYear();
  form.performancePercent = 100;
}

function editEntry(entry) {
  form.id = entry.id;
  form.barberId = entry.barberId;
  form.month = entry.month;
  form.year = entry.year;
  form.performancePercent = Number(entry.performancePercent);
}

async function loadData() {
  barbers.value = await barberService.list();
  entries.value = await hallOfFameService.list();
}

async function submit() {
  submitting.value = true;
  try {
    const payload = {
      barberId: form.barberId,
      month: form.month,
      year: form.year,
      performancePercent: form.performancePercent
    };
    if (form.id) {
      await hallOfFameService.update(form.id, payload);
    } else {
      await hallOfFameService.create(payload);
    }
    await loadData();
    cancelEdit();
  } finally {
    submitting.value = false;
  }
}

async function removeEntry(entry) {
  if (!confirm(`Remover "${entry.barberName}" de ${monthNames[entry.month - 1]}/${entry.year} do Hall da Fama?`)) {
    return;
  }
  try {
    await hallOfFameService.delete(entry.id);
    await loadData();
    if (form.id === entry.id) {
      cancelEdit();
    }
  } catch (e) {
    alert('Não foi possível remover. Tente novamente.');
  }
}

onMounted(() => {
  loadData();
});
</script>

