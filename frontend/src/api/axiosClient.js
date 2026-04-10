import axios from 'axios';
import useAuthStore from '../store/authStore';

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  paramsSerializer: {
    serialize: (params) => {
      const searchParams = new URLSearchParams();
      Object.entries(params).forEach(([key, value]) => {
        if (value === undefined || value === null) return;
        if (Array.isArray(value)) {
          value.forEach(v => searchParams.append(key, v));
        } else {
          searchParams.append(key, value);
        }
      });
      return searchParams.toString();
    }
  },
});

// Request Interceptor: Add Token
axiosClient.interceptors.request.use(
  (config) => {
    const { token } = useAuthStore.getState();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Standardize format and handle 401 (Refresh Token)
axiosClient.interceptors.response.use(
  (response) => {
    if (response && response.data) {
      return response.data;
    }
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    const { refreshToken, setToken, logout } = useAuthStore.getState();

    // Nếu lỗi là 401 và chưa thử lại (để tránh lặp vô hạn)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      if (refreshToken) {
        try {
          // Gọi API refresh chuẩn qua axios mới (để tránh interceptor Token cũ)
          const resp = await axios.post(`${axiosClient.defaults.baseURL}/auth/refresh`, {
            refreshToken: refreshToken
          });

          const { token: newToken } = resp.data.data; // Backend returns ApiResponse<AuthResponse>
          
          // Cập nhật store và retry request gốc
          setToken(newToken);
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return axiosClient(originalRequest);
        } catch (refreshError) {
          // Refresh token cũng hết hạn hoặc lỗi → Logout
          logout();
          window.dispatchEvent(new Event('auth:unauthorized'));
          return Promise.reject(refreshError);
        }
      } else {
        // Không có refresh token → Logout
        logout();
        window.dispatchEvent(new Event('auth:unauthorized'));
      }
    }

    // Return the response data wrapped by backend (AppException standardized format)
    return Promise.reject(error.response?.data || error);
  }
);

export default axiosClient;
