import { Navigate, Outlet } from 'react-router-dom';
import useAuthStore from '../../store/authStore';
import { Result, Button } from 'antd';
import { useNavigate } from 'react-router-dom';

const AdminRoute = () => {
  const { isAuthenticated, user } = useAuthStore();
  const navigate = useNavigate();

  // Redirect to admin login if not logged in at all
  if (!isAuthenticated) {
    return <Navigate to="/admin/login" replace />;
  }

  // Very basic RBAC check
  // Phụ thuộc vào dữ liệu user trả về từ backend (ví dụ user.roles === ['ADMIN'] hoặc user.role === 'ADMIN')
  // Ở đây giả định user có thuộc tính roles là mảng chứa 'ROLE_ADMIN' hoặc 'ADMIN'
  const isAdmin = user?.roles?.includes('ADMIN') || user?.roles?.includes('ROLE_ADMIN') || user?.role === 'ADMIN';

  if (!isAdmin) {
    return (
      <Result
        status="403"
        title="403 Không có quyền"
        subTitle="Xin lỗi, bạn không có quyền truy cập vào trang quản trị."
        extra={<Button type="primary" onClick={() => navigate('/')}>Quay lại trang chủ</Button>}
      />
    );
  }

  // Allowed
  return <Outlet />;
};

export default AdminRoute;
