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

  // Format price (Min - Max range)
  const getPriceRange = () => {
    if (!product.variants || product.variants.length === 0) return 0;
    
    const prices = product.variants.map(v => v.price);
    const minPrice = Math.min(...prices);
    const maxPrice = Math.max(...prices);

    if (minPrice === maxPrice) {
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(minPrice);
    }
    
    return `${new Intl.NumberFormat('vi-VN').format(minPrice)} - ${new Intl.NumberFormat('vi-VN').format(maxPrice)}đ`;
  };

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
              {getPriceRange()}
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
