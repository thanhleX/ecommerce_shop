import AdminLoginForm from './components/AdminLoginForm';

const AdminLoginPage = () => {
  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: '#001529' // Dark theme setup typical for admin
    }}>
      <AdminLoginForm />
    </div>
  );
};

export default AdminLoginPage;
