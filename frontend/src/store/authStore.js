import { create } from 'zustand';

// Attempt to load from localStorage on initial load
const getInitialToken = () => localStorage.getItem('token') || null;
const getInitialRefreshToken = () => localStorage.getItem('refreshToken') || null;
const getInitialUser = () => {
  try {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  } catch (e) {
    return null;
  }
};

// Helper function to safely parse JWT
const parseJwt = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

const getInitialPermissions = () => {
  const token = getInitialToken();
  if (token) {
    const decoded = parseJwt(token);
    return decoded?.permissions || [];
  }
  return [];
};

const useAuthStore = create((set) => ({
  user: getInitialUser(),
  token: getInitialToken(),
  refreshToken: getInitialRefreshToken(),
  permissions: getInitialPermissions(),
  isAuthenticated: !!getInitialToken(),

  login: (user, token, refreshToken) => {
    // Save to localStorage
    localStorage.setItem('token', token);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));

    // Extract permissions
    const decoded = parseJwt(token);
    const permissions = decoded?.permissions || [];

    // Update state
    set({
      user,
      token,
      refreshToken,
      permissions,
      isAuthenticated: true,
    });
  },

  setToken: (token) => {
    localStorage.setItem('token', token);
    const decoded = parseJwt(token);
    const permissions = decoded?.permissions || [];
    set({ token, permissions });
  },

  logout: () => {
    // Remove from localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');

    // Reset state
    set({
      user: null,
      token: null,
      refreshToken: null,
      permissions: [],
      isAuthenticated: false,
    });
  },

  updateUser: (updatedUser) => {
    localStorage.setItem('user', JSON.stringify(updatedUser));
    set({ user: updatedUser });
  }
}));

export default useAuthStore;
