import axiosClient from './axiosClient';

const orderApi = {
  getOrders(params) {
    return axiosClient.get('/orders', { params });
  },

  getById(id) {
    return axiosClient.get(`/orders/${id}`);
  },

  createOrder(data) {
    // data: { paymentMethodId, addressId, note }
    return axiosClient.post('/orders', data);
  },

  cancelOrder(id) {
    return axiosClient.put(`/orders/${id}/cancel`);
  }
};

export default orderApi;
