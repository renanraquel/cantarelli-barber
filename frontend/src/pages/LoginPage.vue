<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h1>Game Cantareli Barber</h1>
        <p class="login-subtitle">Dashboard de performance gamificado</p>
      </div>
      <form class="login-form" @submit.prevent="submit">
        <div class="form-field">
          <label for="username">Usuário</label>
          <input
            id="username"
            v-model="username"
            type="text"
            autocomplete="username"
            required
            :disabled="loading"
          />
        </div>
        <div class="form-field">
          <label for="password">Senha</label>
          <input
            id="password"
            v-model="password"
            type="password"
            autocomplete="current-password"
            required
            :disabled="loading"
          />
        </div>
        <p v-if="error" class="field-error">{{ error }}</p>
        <button type="submit" class="btn-primary btn-login" :disabled="loading">
          {{ loading ? 'Entrando...' : 'Entrar' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { authService } from '../services/api';
import { setAuthUser } from '../stores/auth';

const router = useRouter();
const route = useRoute();
const username = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

async function submit() {
  error.value = '';
  loading.value = true;
  try {
    const data = await authService.login(username.value, password.value);
    setAuthUser(data);
    const redirect = route.query.redirect || '/';
    router.replace(redirect);
  } catch (e) {
    if (e.response?.status === 401) {
      error.value = 'Usuário ou senha inválidos. Tente novamente.';
    } else {
      error.value = 'Erro ao conectar. Verifique se o servidor está rodando.';
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: radial-gradient(circle at top left, rgba(15, 23, 42, 0.95), rgba(2, 6, 23, 0.98));
  border-radius: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.3);
  padding: 2rem;
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.6);
}

.login-header {
  text-align: center;
  margin-bottom: 1.5rem;
}

.login-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #5B8AFF;
}

.login-subtitle {
  margin: 0.5rem 0 0;
  font-size: 0.9rem;
  color: #9ca3af;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.btn-login {
  margin-top: 0.5rem;
  padding: 0.6rem;
  font-size: 1rem;
}
</style>
