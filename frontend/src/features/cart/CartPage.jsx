import { useEffect } from 'react';
import { Table, Button, Typography, InputNumber, Row, Col, Divider, Empty } from 'antd';
import { DeleteOutlined, ShoppingCartOutlined, RightOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';

const { Title, Text } = Typography;

const CartPage = () => {
  const { cart, items, loading, fetchCart, updateQuantity, removeItem } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    fetchCart();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const columns = [
    {
      title: 'Sản phẩm',
      dataIndex: 'productName',
      key: 'productName',
      render: (text, record) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <img src={record.imageUrl || 'https://via.placeholder.com/80'} alt={text} style={{ width: 80, height: 80, objectFit: 'cover', borderRadius: 8 }} />
          <div>
            <Text strong style={{ fontSize: 16 }}>{text}</Text>
            <br />
            <Text type="secondary">Phân loại: {record.size || 'Mặc định'} / {record.color || 'Mặc định'}</Text>
          </div>
        </div>
      ),
    },
    {
      title: 'Đơn giá',
      dataIndex: 'price',
      key: 'price',
      width: 150,
      render: (val) => <Text>{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)}</Text>
    },
    {
      title: 'Số lượng',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 150,
      render: (val, record) => (
        <InputNumber 
          min={1} 
          max={99} 
          value={val} 
          onChange={(newVal) => updateQuantity(record.id, newVal)} 
        />
      )
    },
    {
      title: 'Thành tiền',
      key: 'total',
      width: 150,
      render: (_, record) => (
        <Text strong type="danger">
          {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(record.price * record.quantity)}
        </Text>
      )
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 100,
      align: 'center',
      render: (_, record) => (
        <Button danger type="text" icon={<DeleteOutlined />} onClick={() => removeItem(record.id)} />
      )
    }
  ];

  if (items.length === 0 && !loading) {
    return (
      <div style={{ textAlign: 'center', padding: 100, background: '#fff', borderRadius: 12 }}>
        <ShoppingCartOutlined style={{ fontSize: 80, color: '#d9d9d9', marginBottom: 24 }} />
        <Title level={3} style={{ marginTop: 0 }}>Giỏ hàng của bạn đang trống</Title>
        <Link to="/products">
          <Button type="primary" size="large" style={{ marginTop: 16 }}>Tiếp tục mua sắm</Button>
        </Link>
      </div>
    );
  }

  const totalPrice = items.reduce((acc, item) => acc + (item.price * item.quantity), 0);

  return (
    <div>
      <Title level={2} style={{ marginBottom: 24 }}>Giỏ hàng ({items.length} sản phẩm)</Title>
      
      <Row gutter={[32, 32]}>
        <Col xs={24} lg={16}>
          <div style={{ background: '#fff', padding: 24, borderRadius: 12, border: '1px solid #f0f0f0' }}>
            <Table 
              columns={columns} 
              dataSource={items} 
              rowKey="id" 
              pagination={false}
              loading={loading}
            />
          </div>
        </Col>

        <Col xs={24} lg={8}>
          <div style={{ background: '#fff', padding: 24, borderRadius: 12, border: '1px solid #f0f0f0', position: 'sticky', top: 24 }}>
            <Title level={4}>Tóm tắt đơn hàng</Title>
            <Divider style={{ margin: '16px 0' }} />
            
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
              <Text>Tạm tính:</Text>
              <Text strong>{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(totalPrice)}</Text>
            </div>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
              <Text>Phí giao hàng:</Text>
              <Text type="secondary">Tính tại bước tiếp theo</Text>
            </div>
            
            <Divider style={{ margin: '16px 0' }} />
            
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 24 }}>
              <Text strong style={{ fontSize: 16 }}>Tổng cộng:</Text>
              <Title level={3} type="danger" style={{ margin: 0 }}>
                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(totalPrice)}
              </Title>
            </div>

            <Button 
              type="primary" 
              size="large" 
              block 
              disabled={items.length === 0}
              onClick={() => navigate('/checkout')}
              style={{ height: 50, fontSize: 16, fontWeight: 'bold' }}
            >
              TIẾN HÀNH THANH TOÁN <RightOutlined />
            </Button>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default CartPage;
