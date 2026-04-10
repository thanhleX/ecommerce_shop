import { Card, Typography, Tag, Button } from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import { useCart } from '../../../hooks/useCart';
import { useNavigate } from 'react-router-dom';

const { Meta } = Card;
const { Text } = Typography;

const ProductCard = ({ product }) => {
  const { addToCart } = useCart();
  const thumbnail = product.images?.find(img => img.isThumbnail)?.imageUrl 
                    || product.images?.[0]?.imageUrl 
                    || 'https://via.placeholder.com/300?text=No+Image';

  // Format price (assuming lowest variant price or default)
  const price = product.variants?.[0]?.price || 0;

  const navigate = useNavigate();
  
  return (
    <Card
      hoverable
      cover={
        <Link to={`/products/slug/${product.slug || product.id}`}>
          <div style={{ height: 200, overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16 }}>
            <img alt={product.name} src={thumbnail} style={{ maxWidth: '100%', maxHeight: '100%', objectFit: 'contain', transition: 'transform 0.3s ease' }} className="hover-zoom" />
          </div>
        </Link>
      }
      actions={[
        <Button 
          type="primary" 
          icon={<ShoppingCartOutlined />} 
          size="middle" 
          style={{ width: '85%', borderRadius: 6 }}
          disabled={!product.variants || product.variants.length === 0}
          onClick={(e) => {
            e.preventDefault();
            const firstVariantId = product.variants?.[0]?.id;
            if (firstVariantId) {
              addToCart(firstVariantId, 1);
            }
          }}
        >
          Thêm vào giỏ
        </Button>
      ]}
      style={{ borderRadius: 12, overflow: 'hidden', border: '1px solid #f0f0f0' }}
      styles={{ body: { padding: '16px 20px' } }}
    >
      <Meta
        title={<Link to={`/products/slug/${product.slug || product.id}`} style={{ color: '#000' }}>{product.name}</Link>}
        description={
          <div style={{ marginTop: 8 }}>
            <Text strong type="danger" style={{ fontSize: '1.2rem', display: 'block' }}>
              {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)}
            </Text>
            {product.discount > 0 && (
              <Tag color="error" variant="borderless" style={{ marginTop: 6 }}>-{product.discount}%</Tag>
            )}
          </div>
        }
      />
    </Card>
  );
};

export default ProductCard;
