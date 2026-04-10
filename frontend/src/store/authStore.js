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

const useAuthStore = create((set) => ({
  user: getInitialUser(),
  token: getInitialToken(),
  refreshToken: getInitialRefreshToken(),
  isAuthenticated: !!getInitialToken(),

  login: (user, token, refreshToken) => {
    // Save to localStorage
    localStorage.setItem('token', token);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));

    // Update state
    set({
      user,
      token,
      refreshToken,
      isAuthenticated: true,
    });
  },

  setToken: (token) => {
    localStorage.setItem('token', token);
    set({ token });
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
      isAuthenticated: false,
    });
  },

  updateUser: (updatedUser) => {
    localStorage.setItem('user', JSON.stringify(updatedUser));
    set({ user: updatedUser });
  }
}));

export default useAuthStore;
