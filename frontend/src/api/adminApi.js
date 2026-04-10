import axiosClient from './axiosClient';

const adminApi = {
  // Thống kê Dashboard
  getStats: () => {
    return axiosClient.get('/admin/dashboard');
  },

  // Quản lý Users
  getUsers: (params) => {
    return axiosClient.get('/admin/users', { params });
  },

  toggleUserActive: (id) => {
    return axiosClient.put(`/admin/users/${id}/toggle-active`);
  },

  // Các quản lý khác (Product, Category, Order, etc. có thể dùng chung client API 
  // hoặc nếu backend tách riêng prefix /admin thì ta thêm ở đây. 
  // Dựa vào controllers có AdminOrderController, AdminBlogController...)

  getOrders: (params) => {
    return axiosClient.get('/admin/orders', { params });
  },

  updateOrderStatus: (id, status) => {
    return axiosClient.put(`/admin/orders/${id}/status`, null, {
      params: { status }
    });
  },

  getBlogs: (params) => {
    return axiosClient.get('/admin/blogs', { params });
  },

  deleteBlog: (id) => {
    return axiosClient.delete(`/admin/blogs/${id}`);
  }
};

export default adminApi;
