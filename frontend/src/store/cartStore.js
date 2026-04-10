import { create } from 'zustand';
import cartApi from '../api/cartApi';

const useCartStore = create((set) => ({
  cart: null, // Full cart object (contains total price, etc)
  items: [],
  cartCount: 0, // Number of items in cart
  loading: false,

  fetchCart: async () => {
    set({ loading: true });
    try {
      const response = await cartApi.getCart();
      const cartData = response.data || response;
      set({ 
        cart: cartData, 
        items: cartData.items || [],
        cartCount: cartData.items?.reduce((acc, item) => acc + item.quantity, 0) || 0
      });
    } catch (error) {
      console.error('Error fetching cart:', error);
    } finally {
      set({ loading: false });
    }
  },

  addToCart: async (productVariantId, quantity) => {
    try {
      const response = await cartApi.addToCart({ productVariantId, quantity });
      const cartData = response.data || response;
      set({ 
        cart: cartData, 
        items: cartData.items || [],
        cartCount: cartData.items?.reduce((acc, item) => acc + item.quantity, 0) || 0
      });
      return true;
    } catch (error) {
      console.error('Error adding to cart:', error);
      throw error;
    }
  },

  updateQuantity: async (itemId, quantity) => {
    try {
      const response = await cartApi.updateQuantity(itemId, quantity);
      const cartData = response.data || response;
      set({ 
        cart: cartData, 
        items: cartData.items || [],
        cartCount: cartData.items?.reduce((acc, item) => acc + item.quantity, 0) || 0
      });
    } catch (error) {
      console.error('Error updating quantity:', error);
      throw error;
    }
  },

  removeItem: async (itemId) => {
    try {
      const response = await cartApi.removeItem(itemId);
      const cartData = response.data || response;
      set({ 
        cart: cartData, 
        items: cartData.items || [],
        cartCount: cartData.items?.reduce((acc, item) => acc + item.quantity, 0) || 0
      });
    } catch (error) {
      console.error('Error removing item:', error);
      throw error;
    }
  },

  clearCartLocal: () => {
    set({ cart: null, items: [], cartCount: 0 });
  }
}));

export default useCartStore;
