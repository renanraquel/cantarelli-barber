import { createRouter, createWebHistory } from 'vue-router';
import { authService, setAuthToken } from '../services/api';
import { setAuthUser, clearAuthUser } from '../stores/auth';

import LoginPage from '../pages/LoginPage.vue';
import AdminBarbersPage from '../pages/AdminBarbersPage.vue';
import AdminRevenuePage from '../pages/AdminRevenuePage.vue';
import AdminHallOfFamePage from '../pages/AdminHallOfFamePage.vue';
import DashboardProgressPage from '../pages/DashboardProgressPage.vue';
import DashboardRankingPage from '../pages/DashboardRankingPage.vue';
import DashboardProgressMonthlyPage from '../pages/DashboardProgressMonthlyPage.vue';
import DashboardRankingMonthlyPage from '../pages/DashboardRankingMonthlyPage.vue';
import DashboardHallOfFamePage from '../pages/DashboardHallOfFamePage.vue';
import TvModePage from '../pages/TvModePage.vue';

const routes = [
  { path: '/login', component: LoginPage, meta: { public: true } },
  { path: '/', redirect: '/dashboard/progress' },
  { path: '/admin/barbers', component: AdminBarbersPage, meta: { adminOnly: true } },
  { path: '/admin/revenue', component: AdminRevenuePage, meta: { adminOnly: true } },
  { path: '/admin/hall-of-fame', component: AdminHallOfFamePage, meta: { adminOnly: true } },
  { path: '/dashboard/progress', component: DashboardProgressPage },
  { path: '/dashboard/ranking', component: DashboardRankingPage },
  { path: '/dashboard/progress-monthly', component: DashboardProgressMonthlyPage },
  { path: '/dashboard/ranking-monthly', component: DashboardRankingMonthlyPage },
  { path: '/dashboard/hall-of-fame', component: DashboardHallOfFamePage },
  { path: '/dashboard/tv-mode', component: TvModePage, meta: { adminOnly: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach(async (to) => {
  if (to.meta.public) {
    try {
      const data = await authService.me();
      setAuthUser(data);
      return '/';
    } catch {
      clearAuthUser();
      return true;
    }
  }
  try {
    const data = await authService.me();
    setAuthUser(data);
    if (to.meta.adminOnly && data.role !== 'ADMIN') {
      return '/dashboard/progress';
    }
    return true;
  } catch {
    clearAuthUser();
    setAuthToken(null);
    return { path: '/login', query: { redirect: to.fullPath } };
  }
});

export default router;

