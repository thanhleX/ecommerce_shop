import axiosClient from './axiosClient';

const notificationApi = {
  getNotifications: (params) => {
    return axiosClient.get('/notifications', { params });
  },

  markAsRead: (id) => {
    return axiosClient.put(`/notifications/${id}/read`);
  }
};

export default notificationApi;
