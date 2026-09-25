import { apiClient } from './client.js';

export const categoriesApi = {
  findAll: () => apiClient.get('/categories'),
  create: (name) => apiClient.post('/categories', { name }),
  remove: (id) => apiClient.delete(`/categories/${id}`),
};
