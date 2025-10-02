-- Tam veri dosyası - Tüm örnek veriler

-- Kategoriler
INSERT INTO categories (name, description) VALUES
('Elektronik', 'Elektronik ürünler ve aksesuarları'),
('Giyim', 'Giyim ve aksesuar ürünleri'),
('Ev & Yaşam', 'Ev ve yaşam ürünleri'),
('Spor', 'Spor ve outdoor ürünleri'),
('Kitap', 'Kitap ve dergi'),
('Kozmetik', 'Kozmetik ve kişisel bakım'),
('Oyuncak', 'Oyuncak ve oyun ürünleri'),
('Müzik', 'Müzik enstrümanları ve aksesuarları');

-- Ürünler
INSERT INTO products (name, code, description, price, stock_quantity, min_stock_level, category_id) VALUES
('Gaming Laptop', 'LAP001', 'Yüksek performanslı gaming laptop - RTX 4060, 16GB RAM', 45000.00, 10, 2, 1),
('Akıllı Telefon', 'PHN001', 'Son model akıllı telefon - 128GB, 6.1 inç', 25000.00, 25, 5, 1),
('Kablosuz Kulaklık', 'EAR001', 'Bluetooth kulaklık - Noise Cancelling', 1500.00, 50, 10, 1),
('Spor Ayakkabı', 'SHO001', 'Rahat spor ayakkabı - Koşu için ideal', 500.00, 100, 20, 4),
('Fitness Matı', 'MAT001', 'Yoga ve fitness matı - 6mm kalınlık', 200.00, 75, 15, 4),
('Programlama Kitabı', 'BOK001', 'Java Programlama Dili - Temel Seviye', 100.00, 200, 50, 5),
('Web Tasarım Kitabı', 'BOK002', 'HTML, CSS, JavaScript - Kapsamlı Rehber', 120.00, 150, 30, 5),
('Ruj', 'COS001', 'Uzun süre kalıcı ruj - 12 farklı renk', 80.00, 300, 50, 6),
('Oyuncak Araba', 'TOY001', 'Uzaktan kumandalı oyuncak araba', 150.00, 80, 20, 7),
('Gitar', 'MUS001', 'Klasik gitar - Başlangıç seviyesi', 800.00, 15, 5, 8);

-- Stok hareketleri (örnek)
INSERT INTO stock_movements (product_id, quantity, movement_type, movement_date, reason, created_by) VALUES
(1, 10, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(2, 25, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(3, 50, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(4, 100, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(5, 75, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(6, 200, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(7, 150, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(8, 300, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(9, 80, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1),
(10, 15, 'ENTRY', CURRENT_TIMESTAMP, 'İlk stok girişi', 1);

-- Platformlar
INSERT INTO platforms (name, code, description, api_endpoint, webhook_url, is_active, sync_enabled) VALUES
('eBay', 'EBAY', 'eBay e-ticaret platformu', 'https://api.ebay.com', 'https://your-domain.com/webhooks/ebay', TRUE, TRUE),
('Shopify', 'SHOPIFY', 'Shopify e-ticaret platformu', 'https://your-shop.myshopify.com', 'https://your-domain.com/webhooks/shopify', TRUE, TRUE),
('Amazon', 'AMAZON', 'Amazon e-ticaret platformu', 'https://sellingpartnerapi-eu.amazon.com', 'https://your-domain.com/webhooks/amazon', TRUE, TRUE),
('Trendyol', 'TRENDYOL', 'Trendyol e-ticaret platformu', 'https://api.trendyol.com', 'https://your-domain.com/webhooks/trendyol', TRUE, TRUE),
('Hepsiburada', 'HEPSIBURADA', 'Hepsiburada e-ticaret platformu', 'https://api.hepsiburada.com', 'https://your-domain.com/webhooks/hepsiburada', FALSE, FALSE),
('N11', 'N11', 'N11 e-ticaret platformu', 'https://api.n11.com', 'https://your-domain.com/webhooks/n11', FALSE, FALSE);

-- Platform ürünleri (örnek)
INSERT INTO platform_products (product_id, platform_id, platform_product_id, platform_sku, platform_stock_quantity, last_sync_at) VALUES
-- Gaming Laptop için platform ürünleri
(1, 1, 'EBAY_LAP001', 'SKU-EBAY-LAP001', 10, CURRENT_TIMESTAMP),
(1, 2, 'SHOPIFY_LAP001', 'SKU-SHOPIFY-LAP001', 10, CURRENT_TIMESTAMP),
(1, 3, 'AMAZON_LAP001', 'SKU-AMAZON-LAP001', 10, CURRENT_TIMESTAMP),
(1, 4, 'TRENDYOL_LAP001', 'SKU-TRENDYOL-LAP001', 10, CURRENT_TIMESTAMP),

-- Akıllı Telefon için platform ürünleri
(2, 1, 'EBAY_PHN001', 'SKU-EBAY-PHN001', 25, CURRENT_TIMESTAMP),
(2, 2, 'SHOPIFY_PHN001', 'SKU-SHOPIFY-PHN001', 25, CURRENT_TIMESTAMP),
(2, 3, 'AMAZON_PHN001', 'SKU-AMAZON-PHN001', 25, CURRENT_TIMESTAMP),
(2, 4, 'TRENDYOL_PHN001', 'SKU-TRENDYOL-PHN001', 25, CURRENT_TIMESTAMP),

-- Kablosuz Kulaklık için platform ürünleri
(3, 1, 'EBAY_EAR001', 'SKU-EBAY-EAR001', 50, CURRENT_TIMESTAMP),
(3, 2, 'SHOPIFY_EAR001', 'SKU-SHOPIFY-EAR001', 50, CURRENT_TIMESTAMP),
(3, 3, 'AMAZON_EAR001', 'SKU-AMAZON-EAR001', 50, CURRENT_TIMESTAMP),
(3, 4, 'TRENDYOL_EAR001', 'SKU-TRENDYOL-EAR001', 50, CURRENT_TIMESTAMP);

-- Örnek siparişler
INSERT INTO orders (platform_order_id, platform_id, order_date, total_amount, status, customer_email, shipping_address) VALUES
('EBAY-ORDER-001', 1, CURRENT_TIMESTAMP, 45000.00, 'COMPLETED', 'customer1@example.com', '123 Main St, İstanbul'),
('SHOPIFY-ORDER-002', 2, CURRENT_TIMESTAMP, 25000.00, 'PENDING', 'customer2@example.com', '456 Oak Ave, Ankara'),
('AMAZON-ORDER-003', 3, CURRENT_TIMESTAMP, 1500.00, 'COMPLETED', 'customer3@example.com', '789 Pine St, İzmir'),
('TRENDYOL-ORDER-004', 4, CURRENT_TIMESTAMP, 500.00, 'CANCELLED', 'customer4@example.com', '321 Elm St, Bursa');

-- Sipariş kalemleri
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 45000.00),
(2, 2, 1, 25000.00),
(3, 3, 1, 1500.00),
(4, 4, 1, 500.00);

-- Senkronizasyon logları (örnek)
INSERT INTO sync_logs (product_id, platform_id, sync_type, status, message, sync_date) VALUES
(1, 1, 'STOCK_UPDATE', 'SUCCESS', 'Stok başarıyla güncellendi', CURRENT_TIMESTAMP),
(1, 2, 'STOCK_UPDATE', 'SUCCESS', 'Stok başarıyla güncellendi', CURRENT_TIMESTAMP),
(2, 1, 'STOCK_UPDATE', 'SUCCESS', 'Stok başarıyla güncellendi', CURRENT_TIMESTAMP),
(2, 2, 'STOCK_UPDATE', 'FAILED', 'API bağlantı hatası', CURRENT_TIMESTAMP),
(3, 1, 'PRODUCT_CREATE', 'SUCCESS', 'Ürün başarıyla oluşturuldu', CURRENT_TIMESTAMP),
(3, 2, 'PRODUCT_CREATE', 'PENDING', 'Ürün oluşturma bekleniyor', CURRENT_TIMESTAMP);

-- Ek kullanıcılar (demo için)
INSERT INTO users (username, email, password, first_name, last_name, is_active, email_verified, created_by) VALUES
('manager', 'manager@stoktakip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Manager', 'User', TRUE, TRUE, 1),
('viewer', 'viewer@stoktakip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Viewer', 'User', TRUE, TRUE, 1);

-- Kullanıcı rolleri
INSERT INTO user_roles (user_id, role) VALUES
(2, 'OPERATOR'),
(3, 'MANAGER'),
(4, 'VIEWER');
