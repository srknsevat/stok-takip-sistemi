-- E-ticaret Entegrasyonu için Örnek Veriler

-- Platform verileri
INSERT INTO platforms (name, code, description, is_active, sync_enabled, api_endpoint) VALUES
    ('eBay', 'EBAY', 'eBay Marketplace Entegrasyonu', true, true, 'https://api.ebay.com'),
    ('Shopify', 'SHOPIFY', 'Shopify E-ticaret Platformu', true, true, 'https://your-shop.myshopify.com'),
    ('Amazon', 'AMAZON', 'Amazon Marketplace', true, false, 'https://sellingpartnerapi-eu.amazon.com'),
    ('Trendyol', 'TRENDYOL', 'Trendyol Marketplace', true, false, 'https://api.trendyol.com'),
    ('Hepsiburada', 'HEPSIBURADA', 'Hepsiburada Marketplace', true, false, 'https://mpop.hepsiburada.com');

-- Mevcut ürünleri güncelle
UPDATE products SET 
    is_active = true,
    sync_enabled = true,
    created_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP;

-- Platform ürünleri (örnek)
INSERT INTO platform_products (product_id, platform_id, platform_product_id, platform_sku, platform_title, platform_price, platform_stock_quantity, is_active, is_synced) 
SELECT 
    p.id,
    pl.id,
    p.code || '_' || pl.code,
    p.code,
    p.name,
    p.price,
    p.stock_quantity,
    true,
    true
FROM products p
CROSS JOIN platforms pl
WHERE pl.is_active = true;

-- Örnek siparişler
INSERT INTO orders (order_number, platform_id, customer_name, customer_email, total_amount, order_status, payment_status, platform_order_id) VALUES
    ('EBAY-001', 1, 'Ahmet Yılmaz', 'ahmet@example.com', 45000.00, 'CONFIRMED', 'PAID', 'EBAY123456'),
    ('SHOP-001', 2, 'Ayşe Demir', 'ayse@example.com', 25000.00, 'SHIPPED', 'PAID', 'SHOP789012'),
    ('EBAY-002', 1, 'Mehmet Kaya', 'mehmet@example.com', 12000.00, 'DELIVERED', 'PAID', 'EBAY789012');

-- Örnek sipariş kalemleri
INSERT INTO order_items (order_id, product_id, platform_product_id, quantity, unit_price, total_price, is_processed) VALUES
    (1, 1, 1, 1, 45000.00, 45000.00, true),
    (2, 2, 6, 1, 25000.00, 25000.00, true),
    (3, 3, 11, 1, 12000.00, 12000.00, true);

-- Örnek senkronizasyon logları
INSERT INTO sync_logs (platform_id, product_id, sync_type, sync_status, sync_message, execution_time_ms) VALUES
    (1, 1, 'PRODUCT_CREATE', 'SUCCESS', 'Ürün eBay''de başarıyla oluşturuldu', 1500),
    (2, 1, 'PRODUCT_CREATE', 'SUCCESS', 'Ürün Shopify''de başarıyla oluşturuldu', 1200),
    (1, 2, 'STOCK_UPDATE', 'SUCCESS', 'Stok güncelleme başarılı', 800),
    (2, 2, 'STOCK_UPDATE', 'SUCCESS', 'Stok güncelleme başarılı', 750);
