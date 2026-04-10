import { create } from 'zustand';

const useNotificationStore = create((set, get) => ({
  notifications: [],
  unreadCount: 0,

  setNotifications: (notifications) => {
    const unreadCount = notifications.filter(n => !n.isRead).length;
    set({ notifications, unreadCount });
  },

  addNotification: (notification) => {
    set((state) => {
      const newNotifications = [notification, ...state.notifications];
      const newUnreadCount = state.unreadCount + (notification.isRead ? 0 : 1);
      return {
        notifications: newNotifications,
        unreadCount: newUnreadCount
      };
    });
  },

  markAsRead: (id) => {
    set((state) => {
      const newNotifications = state.notifications.map(n => 
        n.id === id ? { ...n, isRead: true } : n
      );
      const newUnreadCount = Math.max(0, state.unreadCount - 1);
      return {
        notifications: newNotifications,
        unreadCount: newUnreadCount
      };
    });
  },

  clearAll: () => {
    set({ notifications: [], unreadCount: 0 });
  }
}));

export default useNotificationStore;
