import axiosClient from './axiosClient';

const cartApi = {
  getCart() {
    return axiosClient.get('/cart');
  },
  
  addToCart(data) {
    // data: { productVariantId, quantity }
    return axiosClient.post('/cart/items', data);
  },

  updateQuantity(itemId, quantity) {
    return axiosClient.put(`/cart/items/${itemId}?quantity=${quantity}`);
  },

  removeItem(itemId) {
    return axiosClient.delete(`/cart/items/${itemId}`);
  },

  clearCart() {
    return axiosClient.delete('/cart');
  },

  mergeCart(data) {
    // data: { items: [{ productVariantId, quantity }], combine: boolean }
    return axiosClient.post('/cart/merge', data);
  }
};

export default cartApi;
