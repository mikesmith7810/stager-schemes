import { apiClient } from './client.js';

export const suppliersApi = {
  findAll: () => apiClient.get('/suppliers'),
  create: (name) => apiClient.post('/suppliers', { name }),
  remove: (id) => apiClient.delete(`/suppliers/${id}`),
};
