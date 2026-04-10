import axiosClient from './axiosClient';

const productApi = {
  getProducts(params) {
    return axiosClient.get('/products', { params });
  },

  getFeatured() {
    return axiosClient.get('/products/featured');
  },
  
  getById(id) {
    return axiosClient.get(`/products/${id}`);
  },

  getBySlug(slug) {
    return axiosClient.get(`/products/slug/${slug}`);
  },


  // Admin methods
  create: (data) => {
    return axiosClient.post('/admin/products', data);
  },

  update: (id, data) => {
    return axiosClient.put(`/admin/products/${id}`, data);
  },

  delete: (id) => {
    return axiosClient.delete(`/admin/products/${id}`);
  }
};

export default productApi;
