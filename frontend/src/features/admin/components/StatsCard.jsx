import { Card, Statistic } from 'antd';

const StatsCard = ({ title, value, prefix, icon, color = '#1890ff', loading = false }) => {
  return (
    <Card variant="borderless" loading={loading} style={{ borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
      <Statistic
        title={title}
        value={value}
        prefix={prefix || icon}
        styles={{
          value: { color: color, fontWeight: 'bold' },
        }}
      />
    </Card>
  );
};

export default StatsCard;
