import { Modal, Form, Input, Button, message } from 'antd';
import { LockOutlined } from '@ant-design/icons';
import { useAuth } from '../../../hooks/useAuth';

const LinkGoogleModal = ({ visible, onCancel, idToken }) => {
  const { linkGoogle, loading } = useAuth();
  const [form] = Form.useForm();

  const handleLink = async (values) => {
    const result = await linkGoogle(idToken, values.password);
    if (result.success) {
      onCancel(); // Đóng modal nếu thành công
    }
  };

  return (
    <Modal
      title="Liên kết tài khoản Google"
      open={visible}
      onCancel={onCancel}
      footer={null}
      destroyOnClose
    >
      <div style={{ marginBottom: 16 }}>
        Email này đã được đăng ký bằng mật khẩu. 
        Vui lòng nhập mật khẩu tài khoản của bạn để liên kết với Google.
      </div>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleLink}
      >
        <Form.Item
          name="password"
          label="Mật khẩu hiện tại"
          rules={[{ required: true, message: 'Vui lòng nhập mật khẩu!' }]}
        >
          <Input.Password prefix={<LockOutlined />} placeholder="Mật khẩu của bạn" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} style={{ width: '100%' }}>
            Xác nhận liên kết
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default LinkGoogleModal;
