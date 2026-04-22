import { message } from 'antd';
import { useNavigate } from 'react-router-dom';
import useCartStore from '../store/cartStore';
import useAuthStore from '../store/authStore';

export const useCart = () => {
  const cartStore = useCartStore();
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();

  const handleAddToCart = async (productVariantId, quantity = 1, productInfo = null) => {
    try {
      if (!productVariantId) {
        message.warning('Vui lòng chọn phân loại sản phẩm hợp lệ.');
        return false;
      }
      
      const success = await cartStore.addToCart(productVariantId, quantity, productInfo);
      if (success) {
        message.success('Đã thêm sản phẩm vào giỏ hàng!');
        return true;
      }
      return false;
    } catch (error) {
      if (error.response?.status === 401 && isAuthenticated) {
        message.warning('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
        navigate('/login');
      } else {
        message.error(error.message || 'Lỗi khi thêm vào giỏ hàng');
      }
      return false;
    }
  };

  const handleUpdateQuantity = async (itemId, quantity) => {
    try {
      if (quantity <= 0) {
        await cartStore.removeItem(itemId);
        message.success('Đã xóa sản phẩm khỏi giỏ hàng');
      } else {
        await cartStore.updateQuantity(itemId, quantity);
      }
    } catch (error) {
      message.error(error.message || 'Lỗi cập nhật số lượng');
    }
  };

  const handleRemoveItem = async (itemId) => {
    try {
      await cartStore.removeItem(itemId);
      message.success('Đã xóa sản phẩm khỏi giỏ hàng');
    } catch (error) {
      message.error(error.message || 'Lỗi khi xóa sản phẩm');
    }
  };

  return {
    cart: cartStore.cart,
    items: cartStore.items,
    cartCount: cartStore.cartCount,
    loading: cartStore.loading,
    selectedItems: cartStore.selectedItems,
    fetchCart: cartStore.fetchCart,
    initCart: cartStore.initCart,
    addToCart: handleAddToCart,
    updateQuantity: handleUpdateQuantity,
    removeItem: handleRemoveItem,
    toggleSelectItem: cartStore.toggleSelectItem,
    setSelectedItems: cartStore.setSelectedItems,
    mergeGuestCart: cartStore.mergeGuestCart,
    clearCartLocal: cartStore.clearCartLocal
  };
};
