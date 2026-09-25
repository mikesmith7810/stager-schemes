import { apiClient } from './client.js';

export const itemsApi = {
  findAll: () => apiClient.get('/items'),
  findDeleted: () => apiClient.get('/items/bin'),
  create: (item) => apiClient.post('/items', item),
  update: (id, item) => apiClient.put(`/items/${id}`, item),
  delete: (id) => apiClient.delete(`/items/${id}`),
  restore: (id) => apiClient.put(`/items/${id}/restore`, {}),
  emptyBin: () => apiClient.delete('/items/bin'),

  uploadImage: async (id, file) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await fetch(`/api/items/${id}/image`, {
      method: 'POST',
      body: formData,
      credentials: 'include',
    });
    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      throw new Error(data.error || 'Image upload failed');
    }
  },

  deleteImage: async (id) => {
    const res = await fetch(`/api/items/${id}/image`, {
      method: 'DELETE',
      credentials: 'include',
    });
    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      throw new Error(data.error || 'Image delete failed');
    }
  },
};
