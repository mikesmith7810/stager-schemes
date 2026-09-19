import { apiClient } from './client.js';

export const schemesApi = {
  findAll: () => apiClient.get('/schemes'),
  findById: (id) => apiClient.get(`/schemes/${id}`),
  create: (scheme) => apiClient.post('/schemes', scheme),
  update: (id, scheme) => apiClient.put(`/schemes/${id}`, scheme),
  delete: (id) => apiClient.delete(`/schemes/${id}`),
  getSummary: (id) => apiClient.get(`/schemes/${id}/summary`),
  addRoom: (schemeId, payload) => apiClient.post(`/schemes/${schemeId}/rooms`, payload),
  removeRoom: (schemeId, schemeRoomId) =>
    apiClient.delete(`/schemes/${schemeId}/rooms/${schemeRoomId}`),
  addItemToRoom: (schemeId, schemeRoomId, payload) =>
    apiClient.post(`/schemes/${schemeId}/rooms/${schemeRoomId}/items`, payload),
  removeItemFromRoom: (schemeId, schemeRoomId, schemeRoomItemId) =>
    apiClient.delete(`/schemes/${schemeId}/rooms/${schemeRoomId}/items/${schemeRoomItemId}`),
  addPackToRoom: (schemeId, schemeRoomId, payload) =>
    apiClient.post(`/schemes/${schemeId}/rooms/${schemeRoomId}/packs`, payload),
  removePackFromRoom: (schemeId, schemeRoomId, schemeRoomPackId) =>
    apiClient.delete(`/schemes/${schemeId}/rooms/${schemeRoomId}/packs/${schemeRoomPackId}`),
  rename: (id, name) => apiClient.put(`/schemes/${id}/name`, { name }),
  saveCustomerSummaryOverrides: (id, overrides) =>
    apiClient.put(`/schemes/${id}/customer-summary-overrides`, { overrides }),
  setTemplate: (id, template) => apiClient.put(`/schemes/${id}/template`, { template }),
  duplicate: (id, name) => apiClient.post(`/schemes/${id}/duplicate`, { name }),
};
