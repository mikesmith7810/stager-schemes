import { apiClient } from './client.js';

export const itemsApi = {
  findAll: () => apiClient.get('/items'),
  create: (item) => apiClient.post('/items', item),
  update: (id, item) => apiClient.put(`/items/${id}`, item),
  delete: (id) => apiClient.delete(`/items/${id}`),

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
