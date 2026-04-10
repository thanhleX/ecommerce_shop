import axiosClient from './axiosClient';

const addressApi = {
  getAddresses: () => {
    return axiosClient.get('/users/me/addresses');
  },

  createAddress: (data) => {
    return axiosClient.post('/users/me/addresses', data);
  },

  updateAddress: (id, data) => {
    return axiosClient.put(`/users/me/addresses/${id}`, data);
  },

  deleteAddress: (id) => {
    return axiosClient.delete(`/users/me/addresses/${id}`);
  }
};

export default addressApi;
