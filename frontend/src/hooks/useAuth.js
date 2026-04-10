import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { message } from 'antd';
import authApi from '../api/authApi';
import useAuthStore from '../store/authStore';

export const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login: setAuthData, logout: clearAuthData } = useAuthStore();

  const handleLoginSuccess = async (response, redirectUrl) => {
    // response assumes the form { code, data: { token, refreshToken } } based on interceptor
    const token = response.data?.token || response.token;
    const refreshToken = response.data?.refreshToken || response.refreshToken;

    if (!token) {
      throw new Error('Không nhận được token từ server');
    }

    // Temporary store tokens so that getProfile can use it via interceptor
    setAuthData(null, token, refreshToken);

    // Fetch user profile
    const profileRes = await authApi.getProfile();
    const user = profileRes.data || profileRes; // Handle standardized response

    // Save final user data & tokens
    setAuthData(user, token, refreshToken);

    message.success('Đăng nhập thành công!');
    navigate(redirectUrl, { replace: true });
  };

  const customerLogin = async (values) => {
    setLoading(true);
    try {
      const response = await authApi.login(values);
      await handleLoginSuccess(response, '/');
    } catch (error) {
      console.error('Customer Login Error:', error);
      message.error(error?.message || 'Sai tên đăng nhập hoặc mật khẩu!');
      clearAuthData(); // Revert any partial auth state
    } finally {
      setLoading(false);
    }
  };

  const adminLogin = async (values) => {
    setLoading(true);
    try {
      const response = await authApi.login(values);
      // Giả thiết admin và customer dùng chung API login
      // Ta cần check quyền admin ở đây. 
      // Nhưng do data profile chưa gọi ở đây, nên cứ handle success xong sẽ redirect sang /admin. 
      // Route guard của /admin sẽ đá ra ngoài nếu không đủ quyền.
      await handleLoginSuccess(response, '/admin');
    } catch (error) {
      console.error('Admin Login Error:', error);
      message.error(error?.message || 'Sai thông tin hoặc không đủ quyền!');
      clearAuthData();
    } finally {
      setLoading(false);
    }
  };

  const register = async (values) => {
    setLoading(true);
    try {
      const response = await authApi.register(values);
      await handleLoginSuccess(response, '/');
      message.success('Đăng ký tài khoản thành công!');
    } catch (error) {
      console.error('Register Error:', error);
      message.error(error?.message || 'Lỗi đăng ký tài khoản!');
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      // Bỏ qua lỗi (mất mạng, token hết hạn...) để vẫn thực hiện đăng xuất local
      console.error('Logout API Error:', error);
    } finally {
      clearAuthData();
      message.success('Đã đăng xuất!');
      navigate('/login');
    }
  };

  return { customerLogin, adminLogin, register, logout, loading };
};
