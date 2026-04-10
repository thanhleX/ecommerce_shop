import axiosClient from './axiosClient';

const blogApi = {
  getBlogs(params) {
    return axiosClient.get('/blogs', { params });
  },

  getBySlug(slug) {
    return axiosClient.get(`/blogs/${slug}`);
  },

  getCategories() {
    return axiosClient.get('/blog-categories');
  },
  getFeaturedBlogs() {
    return axiosClient.get('/blogs/featured');
  }
};

export default blogApi;
