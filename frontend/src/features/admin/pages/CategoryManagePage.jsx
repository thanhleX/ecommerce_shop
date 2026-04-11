import { useState, useEffect } from 'react';
import { 
  Table, 
  Button, 
  Space, 
  Typography, 
  Modal, 
  Form, 
  Input, 
  Switch, 
  message, 
  Popconfirm,
  Tag,
  Select
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import categoryApi from '../../../api/categoryApi';
import usePermission from '../../../hooks/usePermission';

const { Title } = Typography;
const { Option } = Select;

const CategoryManagePage = () => {
  const { hasPermission } = usePermission();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [form] = Form.useForm();

  const fetchCategories = async () => {
    setLoading(true);
    try {
      const response = await categoryApi.getCategories();
      // Backend returns tree structure in response.data or response
      setCategories(response.data || response);
    } catch (error) {
      console.error('Fetch Categories Error:', error);
      message.error('Không thể lấy danh sách danh mục');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleAdd = () => {
    setEditingCategory(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingCategory(record);
    form.setFieldsValue({
      name: record.name,
      parentId: record.parentId,
      isActive: record.isActive,
      imageUrl: record.imageUrl
    });
    setIsModalVisible(true);
  };

  const handleDelete = async (id) => {
    try {
      await categoryApi.delete(id);
      message.success('Xóa danh mục thành công');
      fetchCategories();
    } catch (error) {
      message.error(error?.message || 'Lỗi khi xóa danh mục');
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      if (editingCategory) {
        await categoryApi.update(editingCategory.id, values);
        message.success('Cập nhật danh mục thành công');
      } else {
        await categoryApi.create(values);
        message.success('Thêm danh mục thành công');
      }
      setIsModalVisible(false);
      fetchCategories();
    } catch (error) {
      if (error?.errorFields) return; // Form validation error
      message.error(error?.message || 'Lỗi khi lưu danh mục');
    }
  };

  // Flatten categories for Select parent option
  const flatCategories = (cats, result = []) => {
    cats.forEach(c => {
      result.push(c);
      if (c.children) flatCategories(c.children, result);
    });
    return result;
  };

  const columns = [
    {
      title: 'Tên danh mục',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Slug',
      dataIndex: 'slug',
      key: 'slug',
    },
    {
      title: 'Trạng thái',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (active) => (
        <Tag color={active ? 'green' : 'red'}>
          {active ? 'Hoạt động' : 'Tạm dừng'}
        </Tag>
      )
    },
    {
      title: 'Hành động',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          {hasPermission('category:manage') && (
            <>
              <Button 
                type="primary" 
                ghost 
                icon={<EditOutlined />} 
                onClick={() => handleEdit(record)}
              />
              <Popconfirm
                title="Bạn có chắc chắn muốn xóa?"
                onConfirm={() => handleDelete(record.id)}
                okText="Xóa"
                cancelText="Hủy"
                okButtonProps={{ danger: true }}
              >
                <Button type="primary" danger ghost icon={<DeleteOutlined />} />
              </Popconfirm>
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <Title level={3}>Quản lý danh mục</Title>
        {hasPermission('category:manage') && (
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            Thêm danh mục
          </Button>
        )}
      </div>

      <Table 
        columns={columns} 
        dataSource={categories} 
        rowKey="id" 
        loading={loading}
        pagination={false}
        // Cho phép hiển thị tree structure nếu backend trả về lồng nhau
        expandable={{ defaultExpandAllRows: true }}
      />

      <Modal
        title={editingCategory ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" initialValues={{ isActive: true }}>
          <Form.Item
            name="name"
            label="Tên danh mục"
            rules={[{ required: true, message: 'Vui lòng nhập tên danh mục!' }]}
          >
            <Input placeholder="ví dụ: Áo Nam" />
          </Form.Item>

          <Form.Item
            name="parentId"
            label="Danh mục cha (nếu có)"
          >
            <Select placeholder="Chọn danh mục cha" allowClear>
              {flatCategories(categories)
                .filter(c => c.isActive) // Chỉ hiện cate đang hoạt động
                .filter(c => !editingCategory || c.id !== editingCategory.id) // Không chọn chính nó làm cha
                .map(c => (
                  <Option key={c.id} value={c.id}>{c.name}</Option>
                ))
              }
            </Select>
          </Form.Item>

          <Form.Item
            name="imageUrl"
            label="Link ảnh"
          >
            <Input placeholder="URL ảnh danh mục" />
          </Form.Item>

          <Form.Item
            name="isActive"
            label="Trạng thái hoạt động"
            valuePropName="checked"
          >
            <Switch checkedChildren="Bật" unCheckedChildren="Tắt" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default CategoryManagePage;
