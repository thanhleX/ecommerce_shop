import { Form, Input, Button, Checkbox, Typography } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useAuth } from '../../../hooks/useAuth';
import { Link } from 'react-router-dom';

const { Title, Text } = Typography;

const CustomerLoginForm = () => {
  const { customerLogin, loading } = useAuth();

  const onFinish = (values) => {
    customerLogin(values);
  };

  return (
    <div style={{ maxWidth: 360, margin: '0 auto', padding: '40px 20px', background: '#fff', borderRadius: 8, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
      <div style={{ textAlign: 'center', marginBottom: 24 }}>
        <Title level={3}>Đăng nhập</Title>
        <Text type="secondary">Chào mừng bạn trở lại E-commerce</Text>
      </div>

      <Form
        name="customer_login"
        initialValues={{ remember: true }}
        onFinish={onFinish}
        size="large"
        layout="vertical"
      >
        <Form.Item
          name="username"
          rules={[{ required: true, message: 'Vui lòng nhập tên đăng nhập!' }]}
        >
          <Input prefix={<UserOutlined />} placeholder="Tên đăng nhập" />
        </Form.Item>

        <Form.Item
          name="password"
          rules={[{ required: true, message: 'Vui lòng nhập mật khẩu!' }]}
        >
          <Input.Password prefix={<LockOutlined />} placeholder="Mật khẩu" />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" style={{ width: '100%' }} loading={loading}>
            Đăng nhập
          </Button>
        </Form.Item>

        <div style={{ textAlign: 'center' }}>
          Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
        </div>
      </Form>
    </div>
  );
};

export default CustomerLoginForm;
