-- Örnek veri ekleme scripti

-- Kullanıcılar
INSERT INTO users (username, email, password, first_name, last_name, role, is_active, created_at, updated_at) VALUES
('admin', 'admin@stoktakip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'User', 'SUPER_ADMIN', true, NOW(), NOW()),
('manager', 'manager@stoktakip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Manager', 'User', 'ADMIN', true, NOW(), NOW()),
('operator', 'operator@stoktakip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Operator', 'User', 'OPERATOR', true, NOW(), NOW());

-- Platformlar
INSERT INTO platforms (name, code, description, is_active, sync_enabled, created_at, updated_at) VALUES
('eBay', 'EBAY', 'eBay e-ticaret platformu', true, true, NOW(), NOW()),
('Shopify', 'SHOPIFY', 'Shopify e-ticaret platformu', true, true, NOW(), NOW()),
('Amazon', 'AMAZON', 'Amazon e-ticaret platformu', true, true, NOW(), NOW()),
('Trendyol', 'TRENDYOL', 'Trendyol e-ticaret platformu', true, true, NOW(), NOW());

-- Malzeme kartları
INSERT INTO material_cards (material_code, material_name, material_type, material_category, unit, current_stock, min_stock_level, max_stock_level, reorder_point, reorder_quantity, standard_cost, average_cost, last_purchase_cost, description, is_active, is_obsolete, created_at, updated_at) VALUES
('MAT001', 'Ana Kart', 'FINISHED_PRODUCT', 'ELECTRONICS', 'ADET', 100.00, 10.00, 200.00, 20.00, 50.00, 150.00, 145.00, 140.00, 'Ana bilgisayar kartı', true, false, NOW(), NOW()),
('MAT002', 'İşlemci', 'COMPONENT', 'ELECTRONICS', 'ADET', 50.00, 5.00, 100.00, 10.00, 25.00, 300.00, 295.00, 290.00, 'Merkezi işlem birimi', true, false, NOW(), NOW()),
('MAT003', 'RAM', 'COMPONENT', 'ELECTRONICS', 'ADET', 200.00, 20.00, 500.00, 50.00, 100.00, 80.00, 78.00, 75.00, 'Bellek modülü', true, false, NOW(), NOW()),
('MAT004', 'Hard Disk', 'COMPONENT', 'ELECTRONICS', 'ADET', 75.00, 10.00, 150.00, 20.00, 50.00, 120.00, 118.00, 115.00, 'Sabit disk', true, false, NOW(), NOW()),
('MAT005', 'Güç Kaynağı', 'COMPONENT', 'ELECTRONICS', 'ADET', 60.00, 5.00, 120.00, 15.00, 30.00, 90.00, 88.00, 85.00, 'Güç kaynağı ünitesi', true, false, NOW(), NOW());

-- BOM (Ürün Ağacı)
INSERT INTO bill_of_materials (parent_material_id, component_material_id, quantity, unit, is_active, created_at, updated_at) VALUES
(1, 2, 1.00, 'ADET', true, NOW(), NOW()),  -- Ana Kart -> İşlemci
(1, 3, 2.00, 'ADET', true, NOW(), NOW()),  -- Ana Kart -> RAM
(1, 4, 1.00, 'ADET', true, NOW(), NOW()),  -- Ana Kart -> Hard Disk
(1, 5, 1.00, 'ADET', true, NOW(), NOW());  -- Ana Kart -> Güç Kaynağı

-- Platform ürünleri
INSERT INTO platform_products (platform_id, product_id, platform_product_id, platform_sku, platform_title, platform_description, platform_price, platform_stock_quantity, is_active, last_sync_at, created_at, updated_at) VALUES
(1, 1, 'EBAY001', 'EBAY-ANA-KART-001', 'Ana Bilgisayar Kartı', 'Yüksek performanslı ana kart', 250.00, 100, true, NOW(), NOW(), NOW()),
(2, 1, 'SHOP001', 'SHOP-ANA-KART-001', 'Ana Bilgisayar Kartı', 'Yüksek performanslı ana kart', 250.00, 100, true, NOW(), NOW(), NOW()),
(3, 1, 'AMZ001', 'AMZ-ANA-KART-001', 'Ana Bilgisayar Kartı', 'Yüksek performanslı ana kart', 250.00, 100, true, NOW(), NOW(), NOW()),
(4, 1, 'TRD001', 'TRD-ANA-KART-001', 'Ana Bilgisayar Kartı', 'Yüksek performanslı ana kart', 250.00, 100, true, NOW(), NOW(), NOW());

-- Siparişler
INSERT INTO orders (platform_id, order_number, customer_name, customer_email, order_date, status, total_amount, currency, created_at, updated_at) VALUES
(1, 'EBAY-001', 'Ahmet Yılmaz', 'ahmet@email.com', NOW(), 'PENDING', 250.00, 'TRY', NOW(), NOW()),
(2, 'SHOP-001', 'Ayşe Demir', 'ayse@email.com', NOW(), 'CONFIRMED', 250.00, 'TRY', NOW(), NOW()),
(3, 'AMZ-001', 'Mehmet Kaya', 'mehmet@email.com', NOW(), 'SHIPPED', 250.00, 'TRY', NOW(), NOW()),
(4, 'TRD-001', 'Fatma Öz', 'fatma@email.com', NOW(), 'DELIVERED', 250.00, 'TRY', NOW(), NOW());

-- Sipariş detayları
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price, created_at, updated_at) VALUES
(1, 1, 1, 250.00, 250.00, NOW(), NOW()),
(2, 1, 1, 250.00, 250.00, NOW(), NOW()),
(3, 1, 1, 250.00, 250.00, NOW(), NOW()),
(4, 1, 1, 250.00, 250.00, NOW(), NOW());

-- Stok hareketleri
INSERT INTO material_stock_movements (material_id, movement_type, quantity, movement_date, description, created_at, updated_at) VALUES
(1, 'INITIAL_STOCK', 100.00, NOW(), 'Başlangıç stoku', NOW(), NOW()),
(2, 'INITIAL_STOCK', 50.00, NOW(), 'Başlangıç stoku', NOW(), NOW()),
(3, 'INITIAL_STOCK', 200.00, NOW(), 'Başlangıç stoku', NOW(), NOW()),
(4, 'INITIAL_STOCK', 75.00, NOW(), 'Başlangıç stoku', NOW(), NOW()),
(5, 'INITIAL_STOCK', 60.00, NOW(), 'Başlangıç stoku', NOW(), NOW());

-- Senkronizasyon logları
INSERT INTO sync_logs (platform_id, sync_type, status, message, sync_date, created_at, updated_at) VALUES
(1, 'STOCK_SYNC', 'SUCCESS', 'Stok senkronizasyonu başarılı', NOW(), NOW(), NOW()),
(2, 'STOCK_SYNC', 'SUCCESS', 'Stok senkronizasyonu başarılı', NOW(), NOW(), NOW()),
(3, 'STOCK_SYNC', 'SUCCESS', 'Stok senkronizasyonu başarılı', NOW(), NOW(), NOW()),
(4, 'STOCK_SYNC', 'SUCCESS', 'Stok senkronizasyonu başarılı', NOW(), NOW(), NOW());