import { apiClient } from './client.js';

async function login(username, password) {
  const body = new URLSearchParams({ username, password });
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body,
    credentials: 'include',
  });
  if (!response.ok) throw new Error('Invalid credentials');
  return response.json();
}

function getCurrentUser() {
  return apiClient.get('/auth/me');
}

function logout() {
  return fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
}

export const authApi = { login, getCurrentUser, logout };
