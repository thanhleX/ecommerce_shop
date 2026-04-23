SET FOREIGN_KEY_CHECKS = 0;

-- ======================
-- DROP TABLE (ngược thứ tự phụ thuộc)
-- ======================
DROP TABLE IF EXISTS voucher_usages;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS product_variants;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS blogs;
DROP TABLE IF EXISTS blog_categories;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS vouchers;
DROP TABLE IF EXISTS payment_methods;
DROP TABLE IF EXISTS invalidated_tokens;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE roles (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE permissions (
  id bigint NOT NULL AUTO_INCREMENT,
  description varchar(255),
  name varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_permissions_name (name)
) ENGINE=InnoDB;

CREATE TABLE payment_methods (
  id bigint NOT NULL AUTO_INCREMENT,
  image text,
  name varchar(100),
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE vouchers (
  id bigint NOT NULL AUTO_INCREMENT,
  created_at datetime(6),
  updated_at datetime(6),
  code varchar(50) NOT NULL,
  end_date datetime(6),
  max_discount double,
  min_order_value double,
  start_date datetime(6),
  status enum('ACTIVE','INACTIVE') NOT NULL,
  type enum('PERCENT','FIXED') NOT NULL,
  usage_limit int,
  usage_per_user int,
  value double NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_vouchers_code (code)
) ENGINE=InnoDB;

CREATE TABLE users (
  id bigint NOT NULL AUTO_INCREMENT,
  created_at datetime(6),
  updated_at datetime(6),
  email varchar(100),
  full_name varchar(100),
  is_active boolean DEFAULT TRUE,
  password varchar(255),
  username varchar(50),
  provider varchar(50) DEFAULT 'local',
  provider_id varchar(255),
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email (email),
  UNIQUE KEY uq_users_username (username)
) ENGINE=InnoDB;

CREATE TABLE blog_categories (
  id bigint NOT NULL AUTO_INCREMENT,
  is_active boolean,
  name varchar(100),
  slug varchar(150),
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE categories (
  id bigint NOT NULL AUTO_INCREMENT,
  image_url text,
  is_active boolean,
  name varchar(100),
  slug varchar(150),
  parent_id bigint,
  PRIMARY KEY (id),
  UNIQUE KEY uq_categories_slug (slug),
  KEY idx_categories_parent_id (parent_id),
  CONSTRAINT fk_categories_parent
    FOREIGN KEY (parent_id) REFERENCES categories (id)
) ENGINE=InnoDB;

CREATE TABLE invalidated_tokens (
  id bigint NOT NULL AUTO_INCREMENT,
  created_at datetime(6),
  expired_at datetime(6) NOT NULL,
  token varchar(512) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_invalidated_tokens_token (token)
) ENGINE=InnoDB;

-- ======================
-- PHỤ THUỘC USERS
-- ======================

CREATE TABLE addresses (
  id bigint NOT NULL AUTO_INCREMENT,
  full_address text,
  is_default boolean,
  phone varchar(20),
  receiver_name varchar(100),
  user_id bigint,
  PRIMARY KEY (id),
  KEY idx_addresses_user_id (user_id),
  CONSTRAINT fk_addresses_users
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE carts (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint,
  PRIMARY KEY (id),
  UNIQUE KEY uq_carts_user_id (user_id),
  CONSTRAINT fk_carts_users
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE refresh_tokens (
  id bigint NOT NULL AUTO_INCREMENT,
  created_at datetime(6),
  expired_at datetime(6) NOT NULL,
  token varchar(512) NOT NULL,
  user_id bigint NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_refresh_tokens_token (token),
  KEY idx_refresh_tokens_user_id (user_id),
  CONSTRAINT fk_refresh_tokens_users
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE notifications (
  id bigint NOT NULL AUTO_INCREMENT,
  created_at datetime(6),
  content text,
  is_read boolean,
  title varchar(255),
  type enum('ORDER','SYSTEM'),
  user_id bigint,
  PRIMARY KEY (id),
  KEY idx_notifications_user_id (user_id),
  CONSTRAINT fk_notifications_users
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  PRIMARY KEY (user_id, role_id),
  KEY idx_user_roles_role_id (role_id),
  CONSTRAINT fk_user_roles_users
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_user_roles_roles
    FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB;

-- ======================
-- PHÂN QUYỀN
-- ======================

CREATE TABLE role_permissions (
  role_id bigint NOT NULL,
  permission_id bigint NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  KEY idx_role_permissions_permission_id (permission_id),
  CONSTRAINT fk_role_permissions_roles
    FOREIGN KEY (role_id) REFERENCES roles (id),
  CONSTRAINT fk_role_permissions_permissions
    FOREIGN KEY (permission_id) REFERENCES permissions (id)
) ENGINE=InnoDB;

-- ======================
-- PRODUCT
-- ======================

CREATE TABLE products (
  id bigint NOT NULL AUTO_INCREMENT,
  created_at datetime(6),
  updated_at datetime(6),
  description text,
  is_active boolean,
  name varchar(255),
  slug varchar(255),
  category_id bigint,
  PRIMARY KEY (id),
  UNIQUE KEY uq_products_slug (slug),
  KEY idx_products_category_id (category_id),
  CONSTRAINT fk_products_categories
    FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB;

CREATE TABLE product_variants (
  id bigint NOT NULL AUTO_INCREMENT,
  attributes text,
  is_active boolean,
  price decimal(12,2),
  quantity int,
  sku varchar(100),
  product_id bigint,
  PRIMARY KEY (id),
  UNIQUE KEY uq_product_variants_sku (sku),
  KEY idx_product_variants_product_id (product_id),
  CONSTRAINT fk_product_variants_products
    FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

CREATE TABLE product_images (
  id bigint NOT NULL AUTO_INCREMENT,
  image_url text,
  is_thumbnail boolean,
  sort_order int,
  product_id bigint,
  PRIMARY KEY (id),
  KEY idx_product_images_product_id (product_id),
  CONSTRAINT fk_product_images_products
    FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

-- ======================
-- CART + ORDER
-- ======================

CREATE TABLE cart_items (
  id bigint NOT NULL AUTO_INCREMENT,
  quantity int,
  cart_id bigint,
  product_variant_id bigint,
  PRIMARY KEY (id),
  KEY idx_cart_items_cart_id (cart_id),
  KEY idx_cart_items_product_variant_id (product_variant_id),
  CONSTRAINT fk_cart_items_carts
    FOREIGN KEY (cart_id) REFERENCES carts (id),
  CONSTRAINT fk_cart_items_product_variants
    FOREIGN KEY (product_variant_id) REFERENCES product_variants (id)
) ENGINE=InnoDB;

CREATE TABLE orders (
  id bigint NOT NULL AUTO_INCREMENT,
  discount_amount decimal(12,2),
  final_amount decimal(12,2),
  note text,
  order_date datetime(6),
  shipping_fee decimal(12,2),
  status enum('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED'),
  total_amount decimal(12,2),
  address_id bigint,
  payment_method_id bigint,
  user_id bigint,
  PRIMARY KEY (id),
  KEY idx_orders_address_id (address_id),
  KEY idx_orders_payment_method_id (payment_method_id),
  KEY idx_orders_user_id (user_id),
  CONSTRAINT fk_orders_users
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_orders_payment_methods
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id),
  CONSTRAINT fk_orders_addresses
    FOREIGN KEY (address_id) REFERENCES addresses (id)
) ENGINE=InnoDB;

CREATE TABLE order_items (
  id bigint NOT NULL AUTO_INCREMENT,
  price decimal(12,2),
  product_name varchar(255),
  quantity int,
  total_amount decimal(12,2),
  variant_attributes text,
  order_id bigint,
  product_variant_id bigint,
  PRIMARY KEY (id),
  KEY idx_order_items_order_id (order_id),
  KEY idx_order_items_product_variant_id (product_variant_id),
  CONSTRAINT fk_order_items_orders
    FOREIGN KEY (order_id) REFERENCES orders (id),
  CONSTRAINT fk_order_items_product_variants
    FOREIGN KEY (product_variant_id) REFERENCES product_variants (id)
) ENGINE=InnoDB;

-- ======================
-- BLOG
-- ======================

CREATE TABLE blogs (
  id bigint NOT NULL AUTO_INCREMENT,
  created_at datetime(6),
  updated_at datetime(6),
  carousel_order int,
  content text,
  is_featured boolean,
  is_published boolean,
  slug varchar(255),
  thumbnail text,
  title varchar(255),
  blog_category_id bigint,
  user_id bigint,
  PRIMARY KEY (id),
  KEY idx_blogs_blog_category_id (blog_category_id),
  KEY idx_blogs_user_id (user_id),
  CONSTRAINT fk_blogs_blog_categories
    FOREIGN KEY (blog_category_id) REFERENCES blog_categories (id),
  CONSTRAINT fk_blogs_users
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- ======================
-- VOUCHER USAGE
-- ======================

CREATE TABLE voucher_usages (
  id bigint NOT NULL AUTO_INCREMENT,
  used_at datetime(6),
  order_id bigint NOT NULL,
  user_id bigint NOT NULL,
  voucher_id bigint NOT NULL,
  PRIMARY KEY (id),
  KEY idx_voucher_usages_order_id (order_id),
  KEY idx_voucher_usages_user_id (user_id),
  KEY idx_voucher_usages_voucher_id (voucher_id),
  CONSTRAINT fk_voucher_usages_orders
    FOREIGN KEY (order_id) REFERENCES orders (id),
  CONSTRAINT fk_voucher_usages_users
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_voucher_usages_vouchers
    FOREIGN KEY (voucher_id) REFERENCES vouchers (id)
) ENGINE=InnoDB;

INSERT INTO roles (id, name) VALUES
(1, 'SUPER_ADMIN'),
(2, 'CUSTOMER'),
(3, 'STAFF');

INSERT INTO permissions (id, name, description) VALUES
(1, 'category:manage', 'Quản lý danh mục sản phẩm'),
(2, 'voucher:manage', 'Quản lý mã giảm giá (Voucher)'),
(3, 'blog:manage', 'Quản lý bài viết và blog'),
(4, 'role:read', 'Xem danh sách vai trò'),
(5, 'role:manage', 'Quản lý vai trò và phân quyền'),
(6, 'product:read', 'Đọc sản phẩm'),
(7, 'product:create', 'Tạo sản phẩm'),
(8, 'product:update', 'Cập nhật sản phẩm'),
(9, 'product:delete', 'Xóa sản phẩm'),
(10, 'order:read', 'Xem đơn hàng'),
(11, 'order:update', 'Cập nhật đơn hàng'),
(12, 'user:read', 'Xem User'),
(13, 'user:manage', 'Quản lý User'),
(14, 'customer:manage', 'Quản lý khách hàng'),
(15, 'staff:manage', 'Quản lý nhân viên');

INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;
