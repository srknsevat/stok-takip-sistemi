-- Önce kategorileri ekle
INSERT INTO categories (name, color, icon) VALUES
    ('Elektronik', '#36A2EB', 'bi-laptop'),
    ('Bilgisayar Parçaları', '#4BC0C0', 'bi-cpu'),
    ('Aksesuar', '#FF6384', 'bi-mouse'),
    ('Kamera & Fotoğraf', '#9966FF', 'bi-camera'),
    ('Yazıcı & Tarayıcı', '#FFCE56', 'bi-printer');

-- Sonra ürünleri ekle
INSERT INTO products (code, name, description, price, stock_quantity, creation_date, categories) VALUES
    ('LAP001', 'Gaming Laptop', '15.6" RTX 4060 Gaming Laptop', 45000.00, 5, CURRENT_DATE(), 'Elektronik'),
    ('PHN001', 'Akıllı Telefon', '6.7" 256GB Akıllı Telefon', 25000.00, 15, CURRENT_DATE(), 'Elektronik'),
    ('TAB001', 'Tablet', '10.9" 64GB Tablet', 12000.00, 20, CURRENT_DATE(), 'Elektronik'),
    
    ('CPU001', 'İşlemci', '12 Çekirdek İşlemci', 8500.00, 25, CURRENT_DATE(), 'Bilgisayar Parçaları'),
    ('RAM001', 'RAM', '32GB DDR5 RAM', 2200.00, 50, CURRENT_DATE(), 'Bilgisayar Parçaları'),
    ('MBD001', 'Anakart', 'ATX Gaming Anakart', 4500.00, 15, CURRENT_DATE(), 'Bilgisayar Parçaları'),
    
    ('MOU001', 'Gaming Mouse', 'Kablosuz Gaming Mouse', 1200.00, 30, CURRENT_DATE(), 'Aksesuar'),
    ('KBD001', 'Mekanik Klavye', 'RGB Mekanik Klavye', 2500.00, 20, CURRENT_DATE(), 'Aksesuar'),
    ('HDP001', 'Kulaklık', 'Kablosuz Oyuncu Kulaklığı', 1800.00, 25, CURRENT_DATE(), 'Aksesuar'),
    
    ('CAM001', 'Dijital Kamera', 'Profesyonel Dijital Kamera', 35000.00, 5, CURRENT_DATE(), 'Kamera & Fotoğraf'),
    ('LEN001', 'Kamera Lensi', '50mm f/1.8 Lens', 8000.00, 10, CURRENT_DATE(), 'Kamera & Fotoğraf'),
    
    ('PRN001', 'Lazer Yazıcı', 'Renkli Lazer Yazıcı', 5500.00, 8, CURRENT_DATE(), 'Yazıcı & Tarayıcı'); 