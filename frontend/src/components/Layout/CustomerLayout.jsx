import { Layout, Menu, Button, Dropdown, Badge, Avatar, List, Typography } from 'antd';
import { ShoppingCartOutlined, UserOutlined, LogoutOutlined, HomeOutlined, AppstoreOutlined, BellOutlined } from '@ant-design/icons';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import useAuthStore from '../../store/authStore';
import useNotificationStore from '../../store/notificationStore';
import { useAuth } from '../../hooks/useAuth';
import { useWebSocket } from '../../hooks/useWebSocket';
import notificationApi from '../../api/notificationApi';
import { useCart } from '../../hooks/useCart';

const { Header, Content, Footer } = Layout;
const { Text } = Typography;

const CustomerLayout = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuthStore();
  const { logout } = useAuth(); // hook handles logout logic including navigation

  // Connect WebSocket & bind store
  useWebSocket();
  const { notifications, unreadCount, setNotifications, markAsRead } = useNotificationStore();

  const { cartCount } = useCart();

  useEffect(() => {
    if (isAuthenticated) {
      notificationApi.getNotifications({ size: 10 })
        .then(res => {
          const data = res.data || res;
          setNotifications(data.content || []);
        })
        .catch(err => console.error("Could not fetch initial notifications", err));
    }
  }, [isAuthenticated, setNotifications]);

  const handleNotificationClick = async (notif) => {
    if (!notif.isRead) {
      try {
        await notificationApi.markAsRead(notif.id);
        markAsRead(notif.id);
      } catch (error) {
        console.error("Failed to mark notification as read", error);
      }
    }
    navigate('/profile');
  };

  const menuItems = [
    { key: '/', icon: <HomeOutlined />, label: <Link to="/">Trang Chủ</Link> },
    { key: '/products', icon: <AppstoreOutlined />, label: <Link to="/products">Sản Phẩm</Link> },
    { key: '/blog', label: <Link to="/blog">Blog</Link> },
  ];

  const userMenuItems = [
    { key: 'profile', label: <Link to="/profile">Hồ sơ</Link>, icon: <UserOutlined /> },
    { type: 'divider' },
    { key: 'logout', label: 'Đăng xuất', icon: <LogoutOutlined />, onClick: logout },
  ];

  const notificationMenu = (
    <div
      style={{
        width: 320,
        background: '#fff',
        borderRadius: 8,
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
        overflow: 'hidden'
      }}
    >
      {/* Header */}
      <div style={{ padding: '12px 16px', fontWeight: 'bold', borderBottom: '1px solid #f0f0f0' }}>
        Thông báo ({unreadCount} chưa đọc)
      </div>

      {/* List */}
      <div style={{ maxHeight: 400, overflowY: 'auto' }}>
        {notifications.length === 0 ? (
          <div style={{ padding: 16, textAlign: 'center', color: '#999' }}>
            Chưa có thông báo nào
          </div>
        ) : (
          notifications.map(item => (
            <div
              key={item.id}
              onClick={() => handleNotificationClick(item)}
              style={{
                padding: '12px 16px',
                cursor: 'pointer',
                background: item.isRead ? '#fff' : '#f0f5ff',
                borderBottom: '1px solid #f0f0f0'
              }}
              onMouseEnter={e => (e.currentTarget.style.background = '#e6f4ff')}
              onMouseLeave={e =>
                (e.currentTarget.style.background = item.isRead ? '#fff' : '#f0f5ff')
              }
            >
              <Text strong={!item.isRead}>{item.title}</Text>
              <br />
              <Text type="secondary" style={{ fontSize: 12 }}>
                {item.content}
              </Text>
            </div>
          ))
        )}
      </div>
    </div>
  );

  return (
    <Layout className="layout" style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', background: '#fff', padding: '0 50px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', zIndex: 1 }}>
        <div className="logo" style={{ float: 'left', width: '120px', height: '31px', margin: '16px 24px 16px 0', background: 'rgba(0, 0, 0, 0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <span><Link to="/" style={{ color: '#fff', fontWeight: 'bold' }}>Ecommerce</Link></span>
        </div>

        <Menu theme="light" mode="horizontal" defaultSelectedKeys={['/']} items={menuItems} style={{ flex: 1, borderBottom: 'none' }} />

        <div style={{ float: 'right', display: 'flex', alignItems: 'center', gap: 24 }}>
          {isAuthenticated && (
            <Dropdown popupRender={() => notificationMenu} placement="bottomRight" trigger={['click']}>
              <Badge count={unreadCount} style={{ cursor: 'pointer' }}>
                <BellOutlined style={{ fontSize: 24, cursor: 'pointer' }} />
              </Badge>
            </Dropdown>
          )}

          <Link to="/cart">
            <Badge count={cartCount} showZero>
              <ShoppingCartOutlined style={{ fontSize: 24 }} />
            </Badge>
          </Link>

          {isAuthenticated ? (
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <span style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
                <Avatar icon={<UserOutlined />} />
                <span>{user?.fullName || user?.username}</span>
              </span>
            </Dropdown>
          ) : (
            <div>
              <Button type="text" onClick={() => navigate('/login')}>Đăng nhập</Button>
              <Button type="primary" onClick={() => navigate('/register')}>Đăng ký</Button>
            </div>
          )}
        </div>
      </Header>

      <Content style={{ padding: '0 50px', marginTop: 24 }}>
        <div style={{ background: '#fff', padding: 24, minHeight: 280, borderRadius: 8 }}>
          <Outlet />
        </div>
      </Content>

      <Footer style={{ textAlign: 'center' }}>
        Ecommerce Design ©{new Date().getFullYear()}
      </Footer>
    </Layout>
  );
};

export default CustomerLayout;
