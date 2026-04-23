import { Navigate, Outlet } from 'react-router-dom';
import useAuthStore from '../../store/authStore';

const GuestRoute = ({ redirectTo = '/' }) => {
  const { isAuthenticated } = useAuthStore();

  if (isAuthenticated) {
    return <Navigate to={redirectTo} replace />;
  }

  return <Outlet />;
};

export default GuestRoute;
