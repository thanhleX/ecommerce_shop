import { Menu, Typography } from 'antd';
import { AppstoreOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const { Title } = Typography;

const CategoryMenu = ({ categories = [], loading }) => {
  const navigate = useNavigate();

  // Helper to build tree for Ant Design Menu
  const buildMenuItems = (cats) => {
    return cats.map(cat => ({
      key: `/products?categoryId=${cat.id}`,
      icon: !cat.parentId ? <AppstoreOutlined /> : null,
      label: cat.name,
      children: cat.children?.length > 0 ? buildMenuItems(cat.children) : undefined
    }));
  };

  const items = loading ? [] : buildMenuItems(categories);

  const onClick = (e) => {
    navigate(e.key);
  };

  return (
    <div style={{ background: '#fff', borderRadius: 8, overflow: 'hidden', border: '1px solid #f0f0f0' }}>
      <div style={{ padding: '16px 24px', borderBottom: '1px solid #f0f0f0', background: '#fafafa' }}>
        <Title level={5} style={{ margin: 0 }}>
          <AppstoreOutlined style={{ marginRight: 8, color: '#1890ff' }} />
          Danh Mục Sản Phẩm
        </Title>
      </div>
      <Menu
        onClick={onClick}
        style={{ width: '100%', borderRight: 0 }}
        mode="inline"
        items={items}
        loading={loading}
      />
    </div>
  );
};

export default CategoryMenu;
