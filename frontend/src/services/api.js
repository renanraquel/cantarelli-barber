import axios from 'axios';

const isDev = import.meta.env.DEV;
export const BACKEND_BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL
  || (isDev ? '' : 'http://localhost:8080');

const api = axios.create({
  baseURL: BACKEND_BASE_URL ? `${BACKEND_BASE_URL}/api` : '/api',
  withCredentials: true
});

export const authService = {
  async login(username, password) {
    const { data } = await api.post('/auth/login', { username, password });
    return data;
  },
  async logout() {
    await api.post('/auth/logout');
  },
  async me() {
    const { data } = await api.get('/auth/me');
    return data;
  }
};

export function resolveBackendUrl(maybeRelativeUrl) {
  if (!maybeRelativeUrl) return '';
  const url = String(maybeRelativeUrl);
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  if (url.startsWith('/')) return `${BACKEND_BASE_URL}${url}`;
  return url;
}

export const barberService = {
  async list() {
    const { data } = await api.get('/barbers');
    return data;
  },
  async create(payload) {
    const { data } = await api.post('/barbers', payload);
    return data;
  },
  async update(id, payload) {
    const { data } = await api.put(`/barbers/${id}`, payload);
    return data;
  },
  async uploadPhoto(id, file) {
    const form = new FormData();
    form.append('file', file);
    const { data } = await api.post(`/barbers/${id}/photo`, form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return data;
  }
};

export const revenueService = {
  async list(barberId = null, page = 0, size = 10) {
    const params = { page, size };
    if (barberId != null) params.barberId = barberId;
    const { data } = await api.get('/revenue', { params });
    return data;
  },
  async get(id) {
    const { data } = await api.get(`/revenue/${id}`);
    return data;
  },
  async create(payload) {
    const { data } = await api.post('/revenue', payload);
    return data;
  },
  async update(id, payload) {
    const { data } = await api.put(`/revenue/${id}`, payload);
    return data;
  },
  async delete(id) {
    await api.delete(`/revenue/${id}`);
  },
  async getCurrentWeek() {
    const { data } = await api.get('/revenue/week');
    return data;
  }
};

export const dashboardService = {
  async getWeeklyProgress() {
    const { data } = await api.get('/dashboard/progress');
    return data;
  },
  async getWeeklyRanking() {
    const { data } = await api.get('/ranking/week');
    return data;
  },
  async getMonthlyProgress() {
    const { data } = await api.get('/dashboard/progress/month');
    return data;
  },
  async getMonthlyRanking() {
    const { data } = await api.get('/ranking/month');
    return data;
  }
};

export const hallOfFameService = {
  async list() {
    const { data } = await api.get('/hall-of-fame');
    return data;
  },
  async getLatest() {
    const { data } = await api.get('/hall-of-fame/latest');
    return data;
  },
  async create(payload) {
    const { data } = await api.post('/hall-of-fame', payload);
    return data;
  },
  async update(id, payload) {
    const { data } = await api.put(`/hall-of-fame/${id}`, payload);
    return data;
  },
  async delete(id) {
    await api.delete(`/hall-of-fame/${id}`);
  }
};

export default api;

