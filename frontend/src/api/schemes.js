import { apiClient } from './client.js';

export const schemesApi = {
  findAll: () => apiClient.get('/schemes'),
  findById: (id) => apiClient.get(`/schemes/${id}`),
  create: (scheme) => apiClient.post('/schemes', scheme),
  delete: (id) => apiClient.delete(`/schemes/${id}`),
  getSummary: (id) => apiClient.get(`/schemes/${id}/summary`),
  addRoom: (schemeId, payload) => apiClient.post(`/schemes/${schemeId}/rooms`, payload),
  removeRoom: (schemeId, schemeRoomId) =>
    apiClient.delete(`/schemes/${schemeId}/rooms/${schemeRoomId}`),
  addItemToRoom: (schemeId, schemeRoomId, payload) =>
    apiClient.post(`/schemes/${schemeId}/rooms/${schemeRoomId}/items`, payload),
  removeItemFromRoom: (schemeId, schemeRoomId, itemId) =>
    apiClient.delete(`/schemes/${schemeId}/rooms/${schemeRoomId}/items/${itemId}`),
  addPackToRoom: (schemeId, schemeRoomId, payload) =>
    apiClient.post(`/schemes/${schemeId}/rooms/${schemeRoomId}/packs`, payload),
  removePackFromRoom: (schemeId, schemeRoomId, packId) =>
    apiClient.delete(`/schemes/${schemeId}/rooms/${schemeRoomId}/packs/${packId}`),
};
