import axiosClient from './axiosClient';

const categoryApi = {
  getCategories(params) {
    return axiosClient.get('/categories', { params });
  },

  getAllCategories(params) {
    return axiosClient.get('/categories/all', { params });
  },

  getById(id) {
    return axiosClient.get(`/categories/${id}`);
  },

  // Admin methods
  create: (data) => {
    return axiosClient.post('/admin/categories', data);
  },

  update: (id, data) => {
    return axiosClient.put(`/admin/categories/${id}`, data);
  },

  delete: (id) => {
    return axiosClient.delete(`/admin/categories/${id}`);
  }
};

export default categoryApi;
