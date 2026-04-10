import { useEffect } from 'react';
import { Typography, Row, Col, Spin, Button } from 'antd';
import { useProducts } from '../../hooks/useProducts';
import ProductCard from '../product/components/ProductCard';
import { Link } from 'react-router-dom';
import BlogCarousel from '../blog/components/BlogCarousel';

const { Title, Paragraph } = Typography;

const HomePage = () => {
  const { products, loading, fetchFeaturedProducts } = useProducts();

  useEffect(() => {
    fetchFeaturedProducts();
  }, [fetchFeaturedProducts]);

  return (
    <div>
      <div style={{ background: '#001529', padding: '60px 20px', textAlign: 'center', color: '#fff', borderRadius: 12, marginBottom: 40, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
        <Title style={{ color: '#fff', margin: 0 }}>Khám Phá Bộ Sưu Tập Mới</Title>
        <Paragraph style={{ color: '#aaa', fontSize: 18, marginTop: 16, marginBottom: 24 }}>Sản phẩm chất lượng cao, thiết kế độc quyền.</Paragraph>
        <Link to="/products">
          <Button type="primary" size="large" style={{ borderRadius: 8, padding: '0 32px' }}>Mua Sắm Ngay</Button>
        </Link>
      </div>

      <Title level={2} style={{ textAlign: 'center', marginBottom: 30 }}>Sản Phẩm Nổi Bật</Title>
      
      {loading ? (
        <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>
      ) : (
        <>
          <Row gutter={[24, 24]}>
            {products.map(product => (
              <Col xs={24} sm={12} md={8} xl={6} key={product.id}>
                <ProductCard product={product} />
              </Col>
            ))}
          </Row>
          <BlogCarousel />
        </>
      )}
    </div>
  );
};

export default HomePage;
