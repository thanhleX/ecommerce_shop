import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import useAuthStore from '../store/authStore';
import useNotificationStore from '../store/notificationStore';
import { notification } from 'antd';

export const useWebSocket = () => {
  const { user, token } = useAuthStore();
  const { addNotification } = useNotificationStore();
  const clientRef = useRef(null);

  useEffect(() => {
    // Only connect if user is logged in
    if (!user || !user.id || !token) {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
      return;
    }

    const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws';

    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      debug: (str) => {
        // console.log('STOMP: ' + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log('Connected to WebSocket');
      
      // Subscribe to user's notification topic
      client.subscribe(`/topic/notifications/${user.id}`, (message) => {
        if (message.body) {
          const notif = JSON.parse(message.body);
          
          // Thêm vào store
          addNotification(notif);
          
          // Hiển thị toast popup
          notification.open({
            message: notif.title,
            description: notif.content,
            placement: 'bottomRight',
            type: 'info',
            duration: 5
          });
        }
      });
    };

    client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [user, token, addNotification]);

  return { client: clientRef.current };
};
