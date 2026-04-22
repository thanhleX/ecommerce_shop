import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Row, Col, Typography, Button, Spin, Tag, InputNumber, Divider, Select, Breadcrumb, Tabs } from 'antd';
import { ShoppingCartOutlined, HomeOutlined } from '@ant-design/icons';
import { useProducts } from '../../hooks/useProducts';
import { useCart } from '../../hooks/useCart';
import ProductGallery from './components/ProductGallery';

const { Title, Text, Paragraph } = Typography;

const ProductDetailPage = () => {
  const { slug } = useParams();
  const { getProductBySlug } = useProducts();
  const { addToCart } = useCart();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [selectedVariant, setSelectedVariant] = useState(null);

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const data = await getProductBySlug(slug);
        setProduct(data);
        if (data.variants && data.variants.length > 0) {
          setSelectedVariant(data.variants[0]);
        }
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };
    if (slug) fetchDetail();
  }, [slug]);

  if (loading) return <div style={{ textAlign: 'center', padding: 100 }}><Spin size="large" /></div>;
  if (!product) return <div style={{ textAlign: 'center', padding: 100, background: '#fff', borderRadius: 8 }}><Title level={3}>Không tìm thấy sản phẩm</Title></div>;

  const handleAddToCart = async () => {
    if (selectedVariant) {
      const productInfo = {
        productName: product.name,
        price: selectedVariant.price,
        imageUrl: product.images?.[0]?.url || '',
        size: selectedVariant.size, // Assuming these exist or handled in productInfo
        color: selectedVariant.color,
        attributes: selectedVariant.attributes
      };
      const success = await addToCart(selectedVariant.id, quantity, productInfo);
      if (success) {
        setQuantity(1);
      }
    }
  };

  const currentPrice = selectedVariant?.price || 0;

  return (
    <div>
      <Breadcrumb
        items={[
          { title: <Link to="/"><HomeOutlined /></Link> },
          { title: <Link to="/products">Sản phẩm</Link> },
          { title: product.category?.name || 'Danh mục' },
        ]}
      />

      <div style={{ background: '#fff', padding: 32, borderRadius: 12, border: '1px solid #f0f0f0' }}>
        <Row gutter={[48, 32]}>
          <Col xs={24} md={10}>
            <ProductGallery images={product.images || []} />
          </Col>
          <Col xs={24} md={14}>
            <Title level={2} style={{ marginTop: 0 }}>{product.name}</Title>

            <div style={{ marginBottom: 16, display: 'flex', gap: 24 }}>
              <Text type="secondary">Mã SKU: <Text strong>{selectedVariant?.sku || 'N/A'}</Text></Text>
            </div>

            <div style={{ background: '#fafafa', padding: '16px 24px', borderRadius: 8, marginBottom: 24 }}>
              <Title level={2} type="danger" style={{ margin: 0 }}>
                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(currentPrice)}
                {product.discount > 0 && <Tag color="error" style={{ marginLeft: 16, fontSize: 16, verticalAlign: 'middle' }}>-{product.discount}%</Tag>}
              </Title>
            </div>

            <Divider />

            {product.variants && product.variants.length > 0 && (
              <div style={{ marginBottom: 24 }}>
                <Text strong style={{ display: 'block', marginBottom: 8 }}>Phân loại sản phẩm:</Text>
                <Select
                  style={{ width: 300 }}
                  size="large"
                  value={selectedVariant?.id}
                  onChange={(val) => {
                    const variant = product.variants.find(v => v.id === val);
                    setSelectedVariant(variant);
                    setQuantity(1); // Reset quantity on variant change
                  }}
                  options={product.variants.map(v => {
                    let labelAttr = v.attributes || 'Mặc định';
                    try {
                      if (v.attributes && v.attributes.startsWith('{')) {
                        const parsed = JSON.parse(v.attributes);
                        // Format: "Màu: Đỏ - Size: L"
                        labelAttr = Object.entries(parsed)
                          .map(([key, value]) => `${key}: ${value}`)
                          .join(' - ');
                      }
                    } catch (e) {
                      // fallback to string
                    }
                    return { value: v.id, label: `${labelAttr} (Kho: ${v.quantity || 0})` };
                  })}
                />
              </div>
            )}

            <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 32 }}>
              <Text strong>Số lượng:</Text>
              <InputNumber
                size="large"
                min={1}
                max={selectedVariant?.quantity || 1}
                value={quantity}
                onChange={setQuantity}
                disabled={!selectedVariant || selectedVariant.quantity < 1}
              />
              <Text type="secondary">{selectedVariant?.quantity || 0} sản phẩm có sẵn</Text>
            </div>

            <Button
              type="primary"
              size="large"
              icon={<ShoppingCartOutlined />}
              onClick={handleAddToCart}
              disabled={!selectedVariant || selectedVariant.quantity < 1}
              style={{ width: '100%', maxWidth: 350, borderRadius: 8, height: 50, fontSize: 16, fontWeight: 'bold' }}
            >
              {(selectedVariant?.quantity || 0) < 1 ? 'HẾT HÀNG' : 'THÊM VÀO GIỎ HÀNG'}
            </Button>
          </Col>
        </Row>
      </div>

      <div style={{ background: '#fff', padding: 32, borderRadius: 12, border: '1px solid #f0f0f0', marginTop: 24 }}>
        <Tabs defaultActiveKey="1" items={[
          {
            label: 'Mô tả sản phẩm',
            key: '1',
            children: <div dangerouslySetInnerHTML={{ __html: product.description?.replace(/\n/g, '<br/>') || 'Chưa có mô tả' }} />
          }
        ]} />
      </div>
    </div>
  );
};

export default ProductDetailPage;
