import React, { useState } from 'react';
import { Card, Typography, Descriptions, Tabs, Form, Input, Button, message } from 'antd';
import useAuthStore from '../../store/authStore';
import OrderHistoryPage from '../order/OrderHistoryPage';
import AddressManager from './components/AddressManager';
import ChangePasswordForm from './components/ChangePasswordForm';
import authApi from '../../api/authApi';

const { Title } = Typography;

const ProfilePage = () => {
  const { user, updateUser } = useAuthStore();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  if (!user) return null;

  const onUpdateProfile = async (values) => {
    setLoading(true);
    try {
      const response = await authApi.updateProfile(values);
      updateUser(response.data);
      message.success('Cập nhật thông tin thành công');
    } catch (error) {
      message.error(error.response?.data?.message || 'Có lỗi xảy ra khi cập nhật hồ sơ');
    } finally {
      setLoading(false);
    }
  };

  const items = [
    {
      key: '1',
      label: 'Thông tin cá nhân',
      children: (
        <Card title="Hồ sơ cá nhân" style={{ width: '100%', maxWidth: 1100, marginLeft: 40 }}>
          <Descriptions column={1} bordered>
            <Descriptions.Item label="Họ và tên">
              {user.fullName || 'Chưa cập nhật'}
            </Descriptions.Item>
            <Descriptions.Item label="Email">
              {user.email || 'Chưa cập nhật'}
            </Descriptions.Item>
            <Descriptions.Item label="Tên đăng nhập">
              {user.username}
            </Descriptions.Item>
          </Descriptions>

          <Form 
            layout="vertical" 
            style={{ marginTop: 32 }} 
            form={form}
            onFinish={onUpdateProfile}
          >
            <Title level={5}>Cập nhật thông tin</Title>
            <Form.Item
              label="Họ và tên"
              name="fullName"
              initialValue={user.fullName}
              rules={[{ required: true, message: 'Vui lòng nhập họ tên' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="Email"
              name="email"
              initialValue={user.email}
              rules={[
                { required: true, message: 'Vui lòng nhập email' },
                { type: 'email', message: 'Email không hợp lệ' }
              ]}
            >
              <Input />
            </Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              Lưu thay đổi
            </Button>
          </Form>
        </Card>
      ),
    },
    {
      key: '2',
      label: 'Sổ địa chỉ',
      children: <AddressManager />,
    },
    {
      key: '3',
      label: 'Đổi mật khẩu',
      children: <ChangePasswordForm />,
    },
    {
      key: '4',
      label: 'Lịch sử đơn hàng',
      children: <OrderHistoryPage />,
    },
  ];

  return (
    <div>
      <Title level={2} style={{ marginBottom: 32, marginLeft: 180 }}>
        Tài khoản của tôi
      </Title>

      <Tabs defaultActiveKey="1" tabPlacement="left" items={items} />
    </div>
  );
};

export default ProfilePage;