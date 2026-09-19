import { apiClient } from './client.js';

export const packsApi = {
  findAll: () => apiClient.get('/packs'),
  create: (pack) => apiClient.post('/packs', pack),
  delete: (id) => apiClient.delete(`/packs/${id}`),
  addItem: (packId, payload) => apiClient.post(`/packs/${packId}/items`, payload),
  removeItem: (packId, itemId) => apiClient.delete(`/packs/${packId}/items/${itemId}`),
};
