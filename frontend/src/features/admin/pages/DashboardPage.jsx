import { useState, useEffect } from 'react';
import { Row, Col, Typography, message, Card, Segmented, Space } from 'antd';
import {
  ShoppingCartOutlined,
  DollarCircleOutlined,
  UserOutlined,
  SkinOutlined
} from '@ant-design/icons';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Cell
} from 'recharts';
import adminApi from '../../../api/adminApi';
import StatsCard from '../components/StatsCard';

const { Title, Text } = Typography;

const DashboardPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [revenueType, setRevenueType] = useState('Week'); // 'Week' or 'Month'

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await adminApi.getStats();
        // response.data (from ApiResponse) or response (if ApiResponse already unwrapped)
        setStats(response.data || response);
      } catch (error) {
        console.error('Fetch Stats Error:', error);
        message.error('Không thể lấy dữ liệu thống kê');
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const chartData = revenueType === 'Week' ? stats?.weeklyRevenue : stats?.monthlyRevenue;

  // Custom tool tip for currency
  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div style={{ backgroundColor: '#fff', padding: '10px', border: '1px solid #ccc', borderRadius: '4px' }}>
          <Text strong>{label}</Text>
          <br />
          <Text type="success" style={{ fontWeight: 'bold' }}>
            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(payload[0].value)}
          </Text>
        </div>
      );
    }
    return null;
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2} style={{ marginBottom: 24 }}>Tổng quan hệ thống</Title>

      <Row gutter={[24, 24]}>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Tổng doanh thu"
            value={stats?.totalRevenue || 0}
            prefix="₫"
            icon={<DollarCircleOutlined />}
            color="#52c41a"
            loading={loading}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Đơn hàng"
            value={stats?.totalOrders || 0}
            icon={<ShoppingCartOutlined />}
            color="#1890ff"
            loading={loading}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Khách hàng"
            value={stats?.totalUsers || 0}
            icon={<UserOutlined />}
            color="#722ed1"
            loading={loading}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Sản phẩm"
            value={stats?.totalProducts || 0}
            icon={<SkinOutlined />}
            color="#fa8c16"
            loading={loading}
          />
        </Col>
      </Row>

      <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
        <Col span={24}>
          <Card 
            title={
                <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
                    <Title level={4} style={{ margin: 0 }}>Biểu đồ doanh thu</Title>
                    <Segmented 
                        options={[{ label: '7 Ngày qua', value: 'Week' }, { label: '6 Tháng qua', value: 'Month' }]} 
                        value={revenueType}
                        onChange={setRevenueType}
                    />
                </div>
            }
            styles={{ body: { padding: '24px 24px 40px 24px' } }}
            style={{ borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}
          >
            <div style={{ width: '100%', height: 350 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                  <XAxis 
                    dataKey="label" 
                    axisLine={false} 
                    tickLine={false} 
                    tick={{ fill: '#8c8c8c', fontSize: 12 }}
                  />
                  <YAxis 
                    axisLine={false} 
                    tickLine={false} 
                    tick={{ fill: '#8c8c8c', fontSize: 12 }}
                    tickFormatter={(value) => `${(value / 1000000).toFixed(1)}M`}
                  />
                  <Tooltip content={<CustomTooltip />} cursor={{ fill: '#f5f5f5' }} />
                  <Bar dataKey="value" radius={[4, 4, 0, 0]} barSize={revenueType === 'Week' ? 40 : 60}>
                    {
                      chartData?.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={revenueType === 'Week' ? '#52c41a' : '#1890ff'} />
                      ))
                    }
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardPage;
