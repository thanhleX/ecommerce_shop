import { useEffect, useState, useMemo } from 'react';
import { Row, Col, Pagination, Spin, Typography, Empty } from 'antd';
import { useProducts } from '../../hooks/useProducts';
import ProductCard from './components/ProductCard';
import ProductFilter from './components/ProductFilter';
import categoryApi from '../../api/categoryApi';
import { useSearchParams } from 'react-router-dom';

const { Title, Text } = Typography;

const DEFAULT_PRICE_RANGE = [0, 3000000];

const ProductListPage = () => {
  const { products, loading, pagination, fetchProducts } = useProducts();
  const [categories, setCategories] = useState([]);
  const [searchParams, setSearchParams] = useSearchParams();

  // 1. Fetch Categories once
  useEffect(() => {
    const fetchCats = async () => {
      try {
        const res = await categoryApi.getCategories();
        setCategories(res.data || res);
      } catch (err) {
        console.error('Failed to fetch categories:', err);
      }
    };
    fetchCats();
  }, []);

  // 2. Build Category Tree
  const categoryTree = useMemo(() => {
    const map = {};
    const tree = [];
    categories.forEach(cat => {
      map[cat.id] = { ...cat, children: [] };
    });
    categories.forEach(cat => {
      if (cat.parentId && map[cat.parentId]) {
        map[cat.parentId].children.push(map[cat.id]);
      } else if (!cat.parentId) {
        tree.push(map[cat.id]);
      }
    });
    return tree;
  }, [categories]);

  // 3. Helper to get all descendant IDs
  const getAllChildIds = (categoryId, flatCats) => {
    const children = flatCats.filter(c => c.parentId === categoryId);
    let ids = [categoryId];
    children.forEach(child => {
      ids = [...ids, ...getAllChildIds(child.id, flatCats)];
    });
    return ids;
  };

  // 4. Parse Filters from URL
  const filtersFromUrl = useMemo(() => {
    const slugs = searchParams.getAll('category');
    const priceStr = searchParams.get('price');
    const page = parseInt(searchParams.get('page') || '1', 10);

    // Map slugs to IDs with Hierarchy Awareness
    let categoryIds = [];
    slugs.forEach(slug => {
      const cat = categories.find(c => c.slug === slug);
      if (cat) {
        const hasChildren = categories.some(c => c.parentId === cat.id);
        if (hasChildren) {
          // If it's a parent, include all descendants
          categoryIds = [...new Set([...categoryIds, ...getAllChildIds(cat.id, categories)])];
        } else {
          // If it's a child, only include itself
          categoryIds = [...new Set([...categoryIds, cat.id])];
        }
      }
    });

    let priceRange = DEFAULT_PRICE_RANGE;
    if (priceStr && priceStr.includes('-')) {
      const parts = priceStr.split('-');
      if (parts.length === 2) {
        const min = parseInt(parts[0], 10);
        const max = parseInt(parts[1], 10);
        if (!isNaN(min) && !isNaN(max)) {
          priceRange = [min, max];
        }
      }
    }

    return { categoryIds, priceRange, page, rawSlugs: slugs };
  }, [searchParams, categories]);

  // 5. Fetch Products on Filter Change
  useEffect(() => {
    if (categories.length > 0 || filtersFromUrl.rawSlugs.length === 0) {
      fetchProducts({
        page: filtersFromUrl.page,
        categoryIds: filtersFromUrl.categoryIds.length > 0 ? filtersFromUrl.categoryIds : undefined,
        minPrice: filtersFromUrl.priceRange[0],
        maxPrice: filtersFromUrl.priceRange[1]
      });
    }
  }, [
    filtersFromUrl.page,
    JSON.stringify(filtersFromUrl.categoryIds),
    JSON.stringify(filtersFromUrl.priceRange),
    categories.length,
    fetchProducts
  ]);

  // 6. Update URL on Filter Change
  const handleFilterChange = ({ categories: newCategoryIds, priceRange: newPriceRange }) => {
    const newParams = new URLSearchParams();

    // Convert selected IDs back to SLUGS for URL
    newCategoryIds.forEach(id => {
      const cat = categories.find(c => c.id === id);
      if (cat) newParams.append('category', cat.slug);
    });

    if (newPriceRange[0] !== DEFAULT_PRICE_RANGE[0] || newPriceRange[1] !== DEFAULT_PRICE_RANGE[1]) {
      newParams.set('price', `${newPriceRange[0]}-${newPriceRange[1]}`);
    }

    newParams.set('page', '1');
    setSearchParams(newParams);
  };

  const handlePageChange = (page) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.set('page', page.toString());
    setSearchParams(newParams);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>Sản Phẩm Của Chúng Tôi</Title>
        <Text type="secondary">Tìm thấy {pagination.total} sản phẩm</Text>
      </div>

      <Row gutter={[32, 24]}>
        <Col xs={24} md={6}>
          <ProductFilter
            categories={categoryTree}
            selectedCategories={filtersFromUrl.categoryIds}
            priceRange={filtersFromUrl.priceRange}
            onFilterChange={handleFilterChange}
          />
        </Col>
        <Col xs={24} md={18}>
          {loading ? (
            <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>
          ) : products.length === 0 ? (
            <div style={{ background: '#fff', padding: 100, borderRadius: 8, border: '1px solid #f0f0f0' }}>
              <Empty description="Không tìm thấy sản phẩm nào phù hợp với bộ lọc" />
            </div>
          ) : (
            <>
              <Row gutter={[24, 24]}>
                {products.map(product => (
                  <Col xs={24} sm={12} lg={8} key={product.id}>
                    <ProductCard product={product} />
                  </Col>
                ))}
              </Row>
              <div style={{ textAlign: 'center', marginTop: 40, background: '#fff', padding: 16, borderRadius: 8, border: '1px solid #f0f0f0' }}>
                <Pagination
                  current={pagination.current}
                  pageSize={pagination.pageSize}
                  total={pagination.total}
                  onChange={handlePageChange}
                  showSizeChanger={false}
                />
              </div>
            </>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ProductListPage;
