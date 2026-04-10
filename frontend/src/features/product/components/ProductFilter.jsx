import { Checkbox, Divider, Slider, Typography, Space, Button, Collapse } from 'antd';
import { useState, useEffect, useMemo } from 'react';

const { Title, Text } = Typography;

const ProductFilter = ({
  categories = [],
  selectedCategories = [],
  priceRange = [0, 3000000],
  onFilterChange
}) => {
  const [localPrice, setLocalPrice] = useState(priceRange);

  const categoryTree = useMemo(() => {
    const map = {};
    const tree = [];
    categories.forEach(cat => map[cat.id] = { ...cat, children: [] });
    categories.forEach(cat => {
      if (cat.parentId && map[cat.parentId]) {
        map[cat.parentId].children.push(map[cat.id]);
      } else if (!cat.parentId) {
        tree.push(map[cat.id]);
      }
    });
    return tree;
  }, [categories]);

  const activeParentKey = useMemo(() => {
    const activeParent = categoryTree.find(parent =>
      selectedCategories.includes(parent.id) ||
      parent.children.some(child => selectedCategories.includes(child.id))
    );
    return activeParent ? activeParent.id.toString() : null;
  }, [categoryTree, selectedCategories]);

  useEffect(() => {
    setLocalPrice(priceRange);
  }, [JSON.stringify(priceRange)]);

  const toggleCategory = (id) => {
    let newSelected = [...selectedCategories];
    const isEditingParent = categoryTree.find(p => p.id === id);

    if (newSelected.includes(id)) {
      // Uncheck
      newSelected = newSelected.filter(item => item !== id);
      if (isEditingParent) {
        // If unchecking a parent, uncheck all its children
        const childIds = isEditingParent.children.map(c => c.id);
        newSelected = newSelected.filter(item => !childIds.includes(item));
      } else {
        // If unchecking a child, also uncheck its parent just in case
        const parent = categoryTree.find(p => p.children.some(c => c.id === id));
        if (parent) {
          newSelected = newSelected.filter(item => item !== parent.id);
        }
      }
    } else {
      // Check
      newSelected.push(id);
      if (isEditingParent) {
        // If checking a parent, check all its children
        isEditingParent.children.forEach(c => {
          if (!newSelected.includes(c.id)) {
            newSelected.push(c.id);
          }
        });
      } else {
        // If checking a child, check if all children are checked
        const parent = categoryTree.find(p => p.children.some(c => c.id === id));
        if (parent) {
          const allChildrenChecked = parent.children.every(c => newSelected.includes(c.id));
          if (allChildrenChecked && !newSelected.includes(parent.id)) {
            newSelected.push(parent.id);
          }
        }
      }
    }
    onFilterChange?.({ categories: newSelected, priceRange });
  };

  const handlePriceChange = (value) => {
    setLocalPrice(value);
  };

  const applyPriceFilter = () => {
    onFilterChange?.({ categories: selectedCategories, priceRange: localPrice });
  };

  // 🔥 NEW: items thay cho Panel
  const collapseItems = categoryTree.map(parent => ({
    key: parent.id.toString(),
    label: (
      <div
        style={{ display: 'flex', alignItems: 'center', gap: 8 }}
        onClick={(e) => e.stopPropagation()}
      >
        <Checkbox
          checked={selectedCategories.includes(parent.id)}
          onChange={() => toggleCategory(parent.id)}
        />
        <span style={{ fontWeight: 500 }}>{parent.name}</span>
      </div>
    ),
    children: (
      <>
        {parent.children.map(child => (
          <div
            key={child.id}
            style={{
              padding: '8px 0 8px 24px',
              display: 'flex',
              alignItems: 'center',
              gap: 8
            }}
          >
            <Checkbox
              checked={selectedCategories.includes(child.id)}
              onChange={() => toggleCategory(child.id)}
            />
            <span>{child.name}</span>
          </div>
        ))}
      </>
    ),
    showArrow: parent.children.length > 0,
    collapsible: parent.children.length > 0 ? 'header' : undefined
  }));

  return (
    <div style={{ padding: '24px', background: '#fff', borderRadius: 8, border: '1px solid #f0f0f0' }}>
      <Title level={5} style={{ marginTop: 0 }}>Danh mục sản phẩm</Title>

      <Collapse
        ghost
        accordion
        items={collapseItems}
        activeKey={activeParentKey ? [activeParentKey] : []}
        expandIconPlacement="end"
        style={{ marginTop: 16 }}
      />

      <Divider />

      <Title level={5}>Khoảng giá</Title>
      <Slider
        range
        step={50000}
        max={3000000}
        value={localPrice}
        onChange={handlePriceChange}
      />

      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }}>
        <Text strong>{new Intl.NumberFormat('vi-VN').format(localPrice[0])}đ</Text>
        <Text strong>{new Intl.NumberFormat('vi-VN').format(localPrice[1])}đ</Text>
      </Space>

      <Button type="primary" onClick={applyPriceFilter} block style={{ borderRadius: 6 }}>
        Áp dụng lọc giá
      </Button>
    </div>
  );
};

export default ProductFilter;