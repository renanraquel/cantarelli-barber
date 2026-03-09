<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">Lançamento de performance diária</div>
        <div class="page-subtitle">
          Registre o valor de performance diária de cada barbeiro. Armazenado internamente e usado
          apenas para cálculos de porcentagem.
        </div>
      </div>
      <span class="tag">Admin • Lançamentos</span>
    </div>

    <div class="grid-2">
      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">{{ form.id ? 'Editar lançamento' : 'Registrar valor' }}</div>
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
            <label>Valor</label>
            <input
              type="text"
              inputmode="numeric"
              :value="currencyDisplay"
              @input="onCurrencyInput"
              placeholder="R$ 0,00"
            />
            <span v-if="valueError" class="field-error">{{ valueError }}</span>
          </div>
          <div class="form-field">
            <label>Data</label>
            <input v-model="form.date" type="date" required />
          </div>
          <div class="form-field" style="align-self: flex-end">
            <button class="btn-primary" type="submit" :disabled="submitting">
              {{ form.id ? 'Atualizar' : 'Salvar valor' }}
            </button>
          </div>
        </form>
        <p class="page-subtitle" style="margin-top: 0.75rem">
          Os valores nunca são exibidos nos dashboards, apenas convertidos em porcentagens de meta.
        </p>
      </section>

      <section class="panel">
        <div class="panel-header">
          <div class="panel-title">Lançamentos</div>
        </div>
        <div class="form-field" style="margin-bottom: 0.75rem">
          <label>Filtrar por barbeiro</label>
          <select v-model="filterBarberId" @change="onFilterChange">
            <option :value="null">Todos</option>
            <option v-for="barber in barbers" :key="barber.id" :value="barber.id">
              {{ barber.name }}
            </option>
          </select>
        </div>
        <div v-if="entries.length === 0 && !totalElements" class="page-subtitle">
          Nenhum lançamento ainda. Ordenação: data (mais recente primeiro).
        </div>
        <template v-else>
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>Barbeiro</th>
                  <th>Data</th>
                  <th>Valor</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="entry in entries" :key="entry.id">
                  <td>{{ entry.barberName }}</td>
                  <td>{{ formatDate(entry.date) }}</td>
                  <td>{{ formatValue(entry.value) }}</td>
                  <td>
                    <button type="button" class="btn-ghost" @click="editEntry(entry)">
                      Editar
                    </button>
                    <button type="button" class="btn-ghost btn-danger" @click="removeEntry(entry)">
                      Remover
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="totalElements > 0" class="pagination">
            <span class="pagination-info">
              Página {{ currentPage + 1 }} de {{ totalPages || 1 }} ({{ totalElements }} lançamentos)
            </span>
            <div v-if="totalPages > 1" class="pagination-buttons">
              <button
                type="button"
                class="btn-ghost"
                :disabled="currentPage === 0"
                @click="goToPage(currentPage - 1)"
              >
                Anterior
              </button>
              <button
                type="button"
                class="btn-ghost"
                :disabled="currentPage >= totalPages - 1"
                @click="goToPage(currentPage + 1)"
              >
                Próxima
              </button>
            </div>
          </div>
        </template>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { barberService, revenueService } from '../services/api';

const barbers = ref([]);
const entries = ref([]);
const submitting = ref(false);
const filterBarberId = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const totalElements = ref(0);
const pageSize = 10;

const today = new Date().toISOString().slice(0, 10);

const form = reactive({
  id: null,
  barberId: '',
  valueStr: '',
  date: today
});

const valueError = ref('');

/** Máscara: dígitos (centavos) → exibição R$ 1.234,56 */
const currencyDisplay = computed(() => {
  const raw = form.valueStr.replace(/\D/g, '') || '0';
  const num = Number(raw) / 100;
  return 'R$ ' + num.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
});

/** Ao digitar: só aceita dígitos e aplica máscara (máx. 12 dígitos = 99.999.999.999,99) */
function onCurrencyInput(e) {
  const digits = (e.target.value || '').replace(/\D/g, '').slice(0, 12);
  form.valueStr = digits;
  valueError.value = '';
}

/** Formata valor para exibição na tabela: R$ 150,50 */
function formatValue(value) {
  if (value == null) return '-';
  return 'R$ ' + Number(value).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

/** Número → dígitos (centavos) para o campo com máscara. Ex: 150.50 → "15050" */
function numberToRaw(num) {
  if (num == null || isNaN(num)) return '';
  const cents = Math.round(Number(num) * 100);
  return String(cents);
}

/** Dígitos (centavos) → número para enviar à API. Ex: "15050" → 150.50 */
function rawToNumber(raw) {
  const digits = (raw || '').replace(/\D/g, '');
  if (!digits) return 0;
  return Number(digits) / 100;
}

function formatDate(dateStr) {
  if (!dateStr) return '-';
  const d = new Date(dateStr + 'T12:00:00');
  return d.toLocaleDateString('pt-BR');
}

function cancelEdit() {
  form.id = null;
  form.barberId = '';
  form.valueStr = '';
  form.date = today;
  valueError.value = '';
}

function editEntry(entry) {
  form.id = entry.id;
  form.barberId = entry.barberId;
  form.valueStr = numberToRaw(entry.value);
  form.date = entry.date;
  valueError.value = '';
}

async function loadBarbers() {
  barbers.value = await barberService.list();
}

async function loadEntries() {
  const data = await revenueService.list(
    filterBarberId.value ?? undefined,
    currentPage.value,
    pageSize
  );
  entries.value = data.content;
  totalPages.value = data.totalPages;
  totalElements.value = data.totalElements;
  currentPage.value = data.number;
}

function onFilterChange() {
  currentPage.value = 0;
  loadEntries();
}

function goToPage(page) {
  currentPage.value = page;
  loadEntries();
}

async function submit() {
  const valueNum = rawToNumber(form.valueStr);
  if (valueNum <= 0) {
    valueError.value = 'Informe um valor maior que zero.';
    return;
  }
  valueError.value = '';
  submitting.value = true;
  try {
    const payload = {
      barberId: form.barberId,
      value: valueNum,
      date: form.date
    };
    if (form.id) {
      await revenueService.update(form.id, payload);
    } else {
      await revenueService.create(payload);
    }
    await loadEntries();
    cancelEdit();
  } finally {
    submitting.value = false;
  }
}

async function removeEntry(entry) {
  if (!confirm(`Remover lançamento de ${entry.barberName} em ${formatDate(entry.date)}?`)) {
    return;
  }
  try {
    await revenueService.delete(entry.id);
    if (form.id === entry.id) {
      cancelEdit();
    }
    if (entries.value.length === 1 && currentPage.value > 0) {
      currentPage.value--;
    }
    await loadEntries();
  } catch (e) {
    alert('Não foi possível remover. Tente novamente.');
  }
}

onMounted(async () => {
  await loadBarbers();
  await loadEntries();
});
</script>
