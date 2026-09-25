import { apiClient } from './client.js';

export const coloursApi = {
  findAll: () => apiClient.get('/colours'),
  create: (name) => apiClient.post('/colours', { name }),
  remove: (id) => apiClient.delete(`/colours/${id}`),
};
