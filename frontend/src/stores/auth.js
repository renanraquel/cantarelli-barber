import { ref } from 'vue';

export const authUser = ref(null);

export function setAuthUser(user) {
  authUser.value = user;
}

export function clearAuthUser() {
  authUser.value = null;
}
