<template>
  <div class="app-container" :class="{ 'tv-mode-fullscreen': isTvMode, 'login-page-container': isLoginPage }">
    <header v-if="!isTvMode && !isLoginPage" class="app-header">
      <div class="logo-title">
        <span class="logo-circle">GC</span>
        <div>
          <h1>Game Cantareli Barber</h1>
          <p class="subtitle">Dashboard de performance gamificado</p>
        </div>
      </div>
      <nav class="main-nav">
        <RouterLink to="/dashboard/progress">Progresso Semanal</RouterLink>
        <RouterLink to="/dashboard/ranking">Ranking Semanal</RouterLink>
        <RouterLink to="/dashboard/ranking-history">Histórico Semanal</RouterLink>
        <RouterLink to="/dashboard/progress-monthly">Progresso Mensal</RouterLink>
        <RouterLink to="/dashboard/ranking-monthly">Ranking Mensal</RouterLink>
        <RouterLink to="/dashboard/hall-of-fame">Hall da Fama</RouterLink>
        <template v-if="isAdmin">
          <RouterLink to="/dashboard/tv-mode">Modo TV</RouterLink>
          <RouterLink to="/admin/barbers">Admin Barbeiros</RouterLink>
          <RouterLink to="/admin/revenue">Admin Lançamentos</RouterLink>
          <RouterLink to="/admin/hall-of-fame">Admin Hall da Fama</RouterLink>
        </template>
        <button type="button" class="btn-logout" @click="logout">Sair</button>
      </nav>
    </header>
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router';
import { authService } from './services/api';
import { authUser, clearAuthUser } from './stores/auth';

const route = useRoute();
const router = useRouter();

const isAdmin = computed(() => authUser.value?.role === 'ADMIN');
const isTvMode = computed(() => route.path === '/dashboard/tv-mode');
const isLoginPage = computed(() => route.path === '/login');

async function logout() {
  try {
    await authService.logout();
  } finally {
    clearAuthUser();
    router.push('/login');
  }
}
</script>

