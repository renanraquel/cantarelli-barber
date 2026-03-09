<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">Barbeiros</div>
        <div class="page-subtitle">Gerencie os barbeiros ativos e suas metas semanais e mensais.</div>
      </div>
      <span class="tag">Admin • Barbeiros</span>
    </div>

    <section class="panel">
      <div class="panel-header">
        <div class="panel-title">Criar / Editar Barbeiro</div>
      </div>
      <form class="form-grid" @submit.prevent="saveBarber">
        <div class="form-field">
          <label>Nome</label>
          <input v-model="form.name" required placeholder="Nome do barbeiro" />
        </div>
        <div class="form-field">
          <label>Foto</label>
          <input type="file" accept="image/png,image/jpeg,image/webp" @change="onFileChange" />
          <div class="page-subtitle">
            Escolha uma foto do seu computador (JPG, PNG ou WEBP, até 5MB).
          </div>
        </div>
        <div class="form-field">
          <label>Meta semanal (unidades)</label>
          <input v-model.number="form.weeklyGoal" type="number" min="0" step="0.01" required />
        </div>
        <div class="form-field">
          <label>Meta mensal (unidades)</label>
          <input v-model.number="form.monthlyGoal" type="number" min="0" step="0.01" required />
        </div>
        <div class="form-field">
          <label>Status</label>
          <select v-model="form.active">
            <option :value="true">Ativo</option>
            <option :value="false">Inativo</option>
          </select>
        </div>
        <div class="form-field" style="align-self: flex-end">
          <button type="submit" class="btn-primary" :disabled="saving">
            {{ form.id ? 'Atualizar barbeiro' : 'Criar barbeiro' }}
          </button>
        </div>
        <div class="form-field" style="align-self: flex-end">
          <button
            type="button"
            class="btn-upload"
            :disabled="!form.id || !selectedFile || uploadingPhoto"
            @click="uploadPhoto"
          >
            {{ uploadingPhoto ? 'Enviando foto...' : 'Enviar foto' }}
          </button>
        </div>
      </form>
      <div v-if="form.photoUrl" style="margin-top: 0.9rem; display: flex; align-items: center; gap: 0.75rem">
        <img :src="resolveBackendUrl(form.photoUrl)" alt="" class="avatar-small" />
        <div class="page-subtitle">Prévia da foto atual</div>
      </div>
    </section>

    <section class="panel">
      <div class="panel-header">
        <div class="panel-title">Barbeiros cadastrados</div>
        <button class="btn-ghost" @click="resetForm">Novo barbeiro</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>Barbeiro</th>
            <th>Meta semanal</th>
            <th>Meta mensal</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="barber in barbers" :key="barber.id">
            <td>
              <div style="display: flex; align-items: center; gap: 0.5rem">
                <img
                  v-if="barber.photoUrl"
                  :src="resolveBackendUrl(barber.photoUrl)"
                  alt=""
                  class="avatar-small"
                />
                <span>{{ barber.name }}</span>
              </div>
            </td>
            <td>
              <span>{{ formatUnits(barber.weeklyGoal) }} pts / semana</span>
            </td>
            <td>
              <span>{{ formatUnits(barber.monthlyGoal) }} pts / mês</span>
            </td>
            <td>
              <span>
                <span
                  class="status-dot"
                  :class="barber.active ? 'status-active' : 'status-inactive'"
                ></span>
                {{ barber.active ? 'Ativo' : 'Inativo' }}
              </span>
            </td>
            <td>
              <button class="btn-ghost" @click="editBarber(barber)">Editar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { barberService, resolveBackendUrl } from '../services/api';

const barbers = ref([]);
const saving = ref(false);
const selectedFile = ref(null);
const uploadingPhoto = ref(false);

const emptyForm = {
  id: null,
  name: '',
  photoUrl: '',
  weeklyGoal: 0,
  monthlyGoal: 0,
  active: true
};

const form = reactive({ ...emptyForm });

function resetForm() {
  Object.assign(form, { ...emptyForm });
  selectedFile.value = null;
}

async function loadBarbers() {
  barbers.value = await barberService.list();
}

function formatUnits(value) {
  if (value == null) return '-';
  return Number(value).toFixed(0);
}

function editBarber(barber) {
  Object.assign(form, {
    id: barber.id,
    name: barber.name,
    photoUrl: barber.photoUrl,
    weeklyGoal: barber.weeklyGoal,
    monthlyGoal: barber.monthlyGoal,
    active: barber.active
  });
}

async function saveBarber() {
  saving.value = true;
  try {
    const payload = {
      name: form.name,
      photoUrl: form.photoUrl,
      weeklyGoal: form.weeklyGoal,
      monthlyGoal: form.monthlyGoal,
      active: form.active
    };
    if (form.id) {
      const updated = await barberService.update(form.id, payload);
      form.photoUrl = updated.photoUrl;
    } else {
      const created = await barberService.create(payload);
      form.id = created.id;
      form.photoUrl = created.photoUrl;
      // Se já tinha foto selecionada, envia automaticamente após criar
      if (selectedFile.value) {
        await uploadPhoto();
      }
    }
    await loadBarbers();
  } finally {
    saving.value = false;
  }
}

function onFileChange(event) {
  const file = event?.target?.files?.[0] || null;
  selectedFile.value = file;
}

async function uploadPhoto() {
  if (!form.id || !selectedFile.value) return;
  uploadingPhoto.value = true;
  try {
    const updated = await barberService.uploadPhoto(form.id, selectedFile.value);
    form.photoUrl = updated.photoUrl;
    await loadBarbers();
    selectedFile.value = null;
  } finally {
    uploadingPhoto.value = false;
  }
}

onMounted(() => {
  loadBarbers();
});
</script>

