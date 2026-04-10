import { useState, useEffect, useCallback } from 'react';
import { 
  Table, 
  Typography, 
  Switch, 
  message, 
  Tag, 
  Space, 
  Avatar, 
  Card
} from 'antd';
import { UserOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import adminApi from '../../../api/adminApi';

const { Title, Text } = Typography;

const UserManagePage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  const fetchUsers = useCallback(async (page = 1, pageSize = 10) => {
    setLoading(true);
    try {
      const response = await adminApi.getUsers({ 
        page: page - 1, 
        size: pageSize 
      });
      const data = response.data || response;
      setUsers(data.content);
      setPagination({
        current: page,
        pageSize: pageSize,
        total: data.totalElements,
      });
    } catch (error) {
      message.error('Không thể lấy danh sách người dùng');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const handleToggleActive = async (id) => {
    try {
      await adminApi.toggleUserActive(id);
      message.success('Cập nhật trạng thái người dùng thành công');
      fetchUsers(pagination.current);
    } catch (error) {
      message.error(error?.message || 'Lỗi khi cập nhật trạng thái');
    }
  };

  const columns = [
    {
      title: 'Khách hàng',
      key: 'user',
      render: (_, record) => (
        <Space>
          <Avatar icon={<UserOutlined />} src={record.avatarUrl} />
          <div>
            <Text strong>{record.username}</Text>
            <br />
            <Text type="secondary" size="small">{record.email}</Text>
          </div>
        </Space>
      )
    },
    {
      title: 'Họ tên',
      dataIndex: 'fullName',
      key: 'fullName',
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'Vai trò',
      dataIndex: 'role',
      key: 'role',
      render: (role) => (
        <Tag color={role === 'ADMIN' ? 'red' : 'blue'}>
          {role}
        </Tag>
      )
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => dayjs(date).format('DD/MM/YYYY'),
    },
    {
      title: 'Kích hoạt',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (active, record) => (
        <Switch 
          checked={active} 
          onChange={() => handleToggleActive(record.id)}
          checkedChildren="Bật"
          unCheckedChildren="Tắt"
        />
      )
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={3} style={{ marginBottom: 24 }}>Quản lý người dùng</Title>

      <Table 
        columns={columns} 
        dataSource={users} 
        rowKey="id" 
        loading={loading}
        pagination={pagination}
        onChange={(p) => fetchUsers(p.current, p.pageSize)}
      />
    </div>
  );
};

export default UserManagePage;
