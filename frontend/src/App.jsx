import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import CustomerLayout from './components/Layout/CustomerLayout';
import AdminLayout from './components/Layout/AdminLayout';
import ProtectedRoute from './components/guards/ProtectedRoute';
import AdminRoute from './components/guards/AdminRoute';

import CustomerLoginPage from './features/auth/CustomerLoginPage';
import AdminLoginPage from './features/auth/AdminLoginPage';
import RegisterPage from './features/auth/RegisterPage';

// Customer Pages
import HomePage from './features/home/HomePage';
import ProductListPage from './features/product/ProductListPage';
import ProductDetailPage from './features/product/ProductDetailPage';
import CartPage from './features/cart/CartPage';
import CheckoutPage from './features/order/CheckoutPage';
import ProfilePage from './features/profile/ProfilePage';
import BlogListPage from './features/blog/BlogListPage';
import BlogDetailPage from './features/blog/BlogDetailPage';

// Admin Pages (Phase 10)
import DashboardPage from './features/admin/pages/DashboardPage';
import ProductManagePage from './features/admin/pages/ProductManagePage';
import CategoryManagePage from './features/admin/pages/CategoryManagePage';
import OrderManagePage from './features/admin/pages/OrderManagePage';
import BlogManagePage from './features/admin/pages/BlogManagePage';
import VoucherManagePage from './features/admin/pages/VoucherManagePage';
import UserManagePage from './features/admin/pages/UserManagePage';
import StaffManagePage from './features/admin/pages/StaffManagePage';
import RoleManagePage from './features/admin/pages/RoleManagePage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* =========================================
            PUBLIC ROUTES
         ========================================= */}
        <Route path="/login" element={<CustomerLoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/admin/login" element={<AdminLoginPage />} />

        {/* =========================================
            CUSTOMER PORTAL (CustomerLayout)
         ========================================= */}
        <Route path="/" element={<CustomerLayout />}>
          {/* Public Pages */}
          <Route index element={<HomePage />} />
          <Route path="products" element={<ProductListPage />} />
          <Route path="products/slug/:slug" element={<ProductDetailPage />} />
          <Route path="blog" element={<BlogListPage />} />
          <Route path="blog/:slug" element={<BlogDetailPage />} />
          
          {/* Protected Customer Pages */}
          <Route element={<ProtectedRoute />}>
            <Route path="cart" element={<CartPage />} />
            <Route path="checkout" element={<CheckoutPage />} />
            <Route path="profile" element={<ProfilePage />} />
            {/* profile/orders handles inside ProfilePage tabs */}
          </Route>
        </Route>

        {/* =========================================
            ADMIN PORTAL (AdminRoute -> AdminLayout)
         ========================================= */}
        <Route path="/admin" element={<AdminRoute />}>
          <Route element={<AdminLayout />}>
            <Route index element={<DashboardPage />} />
            <Route path="products" element={<ProductManagePage />} />
            <Route path="categories" element={<CategoryManagePage />} />
            <Route path="orders" element={<OrderManagePage />} />
            <Route path="users" element={<UserManagePage />} />
            <Route path="staff" element={<StaffManagePage />} />
            <Route path="roles" element={<RoleManagePage />} />
            <Route path="blogs" element={<BlogManagePage />} />
            <Route path="vouchers" element={<VoucherManagePage />} />
          </Route>
        </Route>

        {/* Fallback 404 - Redirect to Home */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
