import { apiClient } from './client.js';

export const roomsApi = {
  findAll: () => apiClient.get('/rooms'),
  create: (room) => apiClient.post('/rooms', room),
  delete: (id) => apiClient.delete(`/rooms/${id}`),
  addItem: (roomId, payload) => apiClient.post(`/rooms/${roomId}/items`, payload),
  removeItem: (roomId, itemId) => apiClient.delete(`/rooms/${roomId}/items/${itemId}`),
  addPack: (roomId, payload) => apiClient.post(`/rooms/${roomId}/packs`, payload),
  removePack: (roomId, packId) => apiClient.delete(`/rooms/${roomId}/packs/${packId}`),
};
