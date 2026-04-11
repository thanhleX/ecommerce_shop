import { Navigate, Outlet } from 'react-router-dom';
import useAuthStore from '../../store/authStore';
import { Result, Button } from 'antd';
import { useNavigate } from 'react-router-dom';

const AdminRoute = () => {
  const { isAuthenticated, user } = useAuthStore();
  const navigate = useNavigate();

  // dark mode of Chrome
  const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;

  // Redirect to admin login if not logged in at all
  if (!isAuthenticated) {
    return <Navigate to="/admin/login" replace />;
  }

  // Hệ thống RBAC: Cho phép vào Admin nếu:
  // 1. Có role ADMIN/SUPER_ADMIN/STAFF (với tiền tố ROLE_)
  // 2. HOẶC sở hữu bất kỳ quyền quản trị nào (permissions không trống)
  const isStaffOrAdmin = user?.roles?.some(r =>
    ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF'].includes(r)
  ) || (user?.permissions && user.permissions.length > 0);

  if (!isStaffOrAdmin) {
    return (
      <Result
        status="403"
        title={
          <span style={{ color: isDarkMode ? '#fff' : '#000' }}>
            403 Không có quyền
          </span>
        }
        subTitle={
          <span style={{ color: isDarkMode ? '#aaa' : '#555' }}>
            Xin lỗi, bạn không có quyền truy cập vào trang quản trị.
          </span>
        }
        extra={
          <Button type="primary" onClick={() => navigate('/')}>
            Quay lại trang chủ
          </Button>
        }
      />
    );
  }

  // Allowed
  return <Outlet />;
};

export default AdminRoute;
