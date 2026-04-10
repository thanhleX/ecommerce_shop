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
  }
};

export default cartApi;
