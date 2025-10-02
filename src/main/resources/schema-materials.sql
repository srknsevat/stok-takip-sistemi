-- Malzeme kartı ve BOM tabloları
DROP TABLE IF EXISTS material_stock_movements;
DROP TABLE IF EXISTS bill_of_materials;
DROP TABLE IF EXISTS material_cards;

-- Malzeme kartları tablosu
CREATE TABLE material_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(50) NOT NULL UNIQUE,
    material_name VARCHAR(255) NOT NULL,
    description TEXT,
    material_type VARCHAR(20) NOT NULL, -- RAW_MATERIAL, SEMI_FINISHED, FINISHED_GOOD, CONSUMABLE, TOOL, EQUIPMENT
    material_category VARCHAR(20) NOT NULL, -- ELECTRONIC, MECHANICAL, CHEMICAL, TEXTILE, FOOD, CONSTRUCTION, AUTOMOTIVE, MEDICAL, OTHER
    unit_of_measure VARCHAR(10),
    weight DECIMAL(10,4),
    dimensions VARCHAR(100),
    volume DECIMAL(10,4),
    current_stock DECIMAL(10,4) DEFAULT 0,
    min_stock_level DECIMAL(10,4) DEFAULT 0,
    max_stock_level DECIMAL(10,4) DEFAULT 0,
    reorder_point DECIMAL(10,4) DEFAULT 0,
    reorder_quantity DECIMAL(10,4) DEFAULT 0,
    standard_cost DECIMAL(10,2) DEFAULT 0,
    average_cost DECIMAL(10,2) DEFAULT 0,
    last_purchase_cost DECIMAL(10,2) DEFAULT 0,
    selling_price DECIMAL(10,2) DEFAULT 0,
    supplier_code VARCHAR(50),
    supplier_name VARCHAR(255),
    lead_time_days INT DEFAULT 0,
    shelf_life_days INT DEFAULT 0,
    batch_controlled BOOLEAN DEFAULT FALSE,
    serial_controlled BOOLEAN DEFAULT FALSE,
    quality_grade VARCHAR(10),
    certification_required BOOLEAN DEFAULT FALSE,
    inspection_required BOOLEAN DEFAULT FALSE,
    storage_location VARCHAR(100),
    storage_conditions VARCHAR(255),
    hazardous_material BOOLEAN DEFAULT FALSE,
    hazard_class VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    is_obsolete BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- BOM (Bill of Material) tablosu
CREATE TABLE bill_of_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_material_id BIGINT NOT NULL,
    child_material_id BIGINT NOT NULL,
    quantity DECIMAL(10,4) NOT NULL,
    unit_of_measure VARCHAR(10),
    bom_level INT DEFAULT 1,
    bom_type VARCHAR(20) NOT NULL DEFAULT 'PRODUCTION', -- PRODUCTION, ENGINEERING, COSTING, PLANNING, SALES
    alternative_bom VARCHAR(50),
    is_primary BOOLEAN DEFAULT TRUE,
    yield_percentage DECIMAL(5,2) DEFAULT 100.00,
    scrap_percentage DECIMAL(5,2) DEFAULT 0.00,
    unit_cost DECIMAL(10,2) DEFAULT 0,
    total_cost DECIMAL(10,2) DEFAULT 0,
    operation_sequence INT DEFAULT 1,
    operation_name VARCHAR(255),
    work_center VARCHAR(100),
    setup_time DECIMAL(10,2) DEFAULT 0,
    run_time DECIMAL(10,2) DEFAULT 0,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    is_phantom BOOLEAN DEFAULT FALSE,
    notes TEXT,
    FOREIGN KEY (parent_material_id) REFERENCES material_cards(id) ON DELETE CASCADE,
    FOREIGN KEY (child_material_id) REFERENCES material_cards(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id),
    UNIQUE KEY unique_bom (parent_material_id, child_material_id, bom_type)
);

-- Malzeme stok hareketleri tablosu
CREATE TABLE material_stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL,
    quantity DECIMAL(10,4) NOT NULL,
    movement_type VARCHAR(20) NOT NULL, -- IN, OUT, TRANSFER, ADJUSTMENT, PRODUCTION_IN, PRODUCTION_OUT, SALES, PURCHASE, RETURN, SCRAP
    movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reference_number VARCHAR(100),
    reference_type VARCHAR(50), -- PURCHASE, PRODUCTION, SALES, TRANSFER, ADJUSTMENT
    unit_cost DECIMAL(10,2),
    total_cost DECIMAL(10,2),
    batch_number VARCHAR(100),
    serial_number VARCHAR(100),
    expiry_date TIMESTAMP,
    location VARCHAR(100),
    reason VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (material_id) REFERENCES material_cards(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- İndeksler
CREATE INDEX idx_material_cards_code ON material_cards(material_code);
CREATE INDEX idx_material_cards_name ON material_cards(material_name);
CREATE INDEX idx_material_cards_type ON material_cards(material_type);
CREATE INDEX idx_material_cards_category ON material_cards(material_category);
CREATE INDEX idx_material_cards_active ON material_cards(is_active);
CREATE INDEX idx_material_cards_supplier ON material_cards(supplier_code);
CREATE INDEX idx_material_cards_storage ON material_cards(storage_location);

CREATE INDEX idx_bom_parent ON bill_of_materials(parent_material_id);
CREATE INDEX idx_bom_child ON bill_of_materials(child_material_id);
CREATE INDEX idx_bom_type ON bill_of_materials(bom_type);
CREATE INDEX idx_bom_level ON bill_of_materials(bom_level);
CREATE INDEX idx_bom_active ON bill_of_materials(is_active);
CREATE INDEX idx_bom_effective ON bill_of_materials(effective_from, effective_to);

CREATE INDEX idx_material_movements_material ON material_stock_movements(material_id);
CREATE INDEX idx_material_movements_date ON material_stock_movements(movement_date);
CREATE INDEX idx_material_movements_type ON material_stock_movements(movement_type);
CREATE INDEX idx_material_movements_reference ON material_stock_movements(reference_number);

-- Örnek malzeme kartları
INSERT INTO material_cards (material_code, material_name, description, material_type, material_category, unit_of_measure, weight, current_stock, min_stock_level, max_stock_level, standard_cost, selling_price, supplier_code, supplier_name) VALUES
-- Hammadde
('M001', 'Çelik Levha', '2mm kalınlığında çelik levha', 'RAW_MATERIAL', 'MECHANICAL', 'M2', 15.5, 100.00, 20.00, 200.00, 50.00, 75.00, 'SUP001', 'Çelik A.Ş.'),
('M002', 'Alüminyum Profil', '40x40mm alüminyum profil', 'RAW_MATERIAL', 'MECHANICAL', 'M', 2.1, 500.00, 100.00, 1000.00, 25.00, 40.00, 'SUP002', 'Alüminyum Ltd.'),
('M003', 'Elektronik Kart', 'Ana kontrol kartı', 'RAW_MATERIAL', 'ELECTRONIC', 'ADET', 0.1, 50.00, 10.00, 100.00, 150.00, 250.00, 'SUP003', 'Elektronik San.'),
('M004', 'Kablo', '2.5mm2 güç kablosu', 'RAW_MATERIAL', 'ELECTRONIC', 'M', 0.5, 1000.00, 200.00, 2000.00, 5.00, 8.00, 'SUP004', 'Kablo A.Ş.'),

-- Yarı Mamul
('M005', 'Gövde Parçası', 'Çelik levhadan kesilmiş gövde', 'SEMI_FINISHED', 'MECHANICAL', 'ADET', 12.0, 25.00, 5.00, 50.00, 80.00, 120.00, NULL, NULL),
('M006', 'Montaj Grubu', 'Elektronik kart ve kablolar', 'SEMI_FINISHED', 'ELECTRONIC', 'ADET', 0.5, 15.00, 3.00, 30.00, 200.00, 300.00, NULL, NULL),

-- Mamul
('M007', 'Elektrikli Motor', '1.5kW elektrikli motor', 'FINISHED_GOOD', 'MECHANICAL', 'ADET', 25.0, 10.00, 2.00, 20.00, 500.00, 750.00, NULL, NULL),
('M008', 'Kontrol Paneli', 'Tam montajlı kontrol paneli', 'FINISHED_GOOD', 'ELECTRONIC', 'ADET', 15.0, 5.00, 1.00, 10.00, 800.00, 1200.00, NULL, NULL),

-- Sarf Malzeme
('M009', 'Vida M6x20', 'Paslanmaz çelik vida', 'CONSUMABLE', 'MECHANICAL', 'ADET', 0.01, 1000.00, 100.00, 2000.00, 0.50, 1.00, 'SUP005', 'Vida San.'),
('M010', 'Kablo Bağlantı Ucu', 'Kablo bağlantı ucu', 'CONSUMABLE', 'ELECTRONIC', 'ADET', 0.005, 500.00, 50.00, 1000.00, 0.25, 0.50, 'SUP006', 'Bağlantı Ltd.');

-- Örnek BOM yapısı
INSERT INTO bill_of_materials (parent_material_id, child_material_id, quantity, bom_type, bom_level, unit_cost, total_cost, operation_sequence, operation_name, work_center) VALUES
-- Elektrikli Motor BOM
((SELECT id FROM material_cards WHERE material_code = 'M007'), (SELECT id FROM material_cards WHERE material_code = 'M005'), 1.0000, 'PRODUCTION', 1, 80.00, 80.00, 1, 'Gövde Montajı', 'WC001'),
((SELECT id FROM material_cards WHERE material_code = 'M007'), (SELECT id FROM material_cards WHERE material_code = 'M006'), 1.0000, 'PRODUCTION', 1, 200.00, 200.00, 2, 'Elektronik Montajı', 'WC002'),
((SELECT id FROM material_cards WHERE material_cards WHERE material_code = 'M007'), (SELECT id FROM material_cards WHERE material_code = 'M009'), 8.0000, 'PRODUCTION', 1, 0.50, 4.00, 3, 'Vida Montajı', 'WC001'),
((SELECT id FROM material_cards WHERE material_code = 'M007'), (SELECT id FROM material_cards WHERE material_code = 'M010'), 4.0000, 'PRODUCTION', 1, 0.25, 1.00, 4, 'Kablo Bağlantısı', 'WC002'),

-- Kontrol Paneli BOM
((SELECT id FROM material_cards WHERE material_code = 'M008'), (SELECT id FROM material_cards WHERE material_code = 'M003'), 1.0000, 'PRODUCTION', 1, 150.00, 150.00, 1, 'Kart Montajı', 'WC003'),
((SELECT id FROM material_cards WHERE material_code = 'M008'), (SELECT id FROM material_cards WHERE material_code = 'M004'), 2.0000, 'PRODUCTION', 1, 5.00, 10.00, 2, 'Kablo Montajı', 'WC003'),
((SELECT id FROM material_cards WHERE material_code = 'M008'), (SELECT id FROM material_cards WHERE material_code = 'M002'), 1.5000, 'PRODUCTION', 1, 25.00, 37.50, 3, 'Profil Montajı', 'WC001'),
((SELECT id FROM material_cards WHERE material_code = 'M008'), (SELECT id FROM material_cards WHERE material_code = 'M009'), 12.0000, 'PRODUCTION', 1, 0.50, 6.00, 4, 'Vida Montajı', 'WC001'),
((SELECT id FROM material_cards WHERE material_code = 'M008'), (SELECT id FROM material_cards WHERE material_code = 'M010'), 6.0000, 'PRODUCTION', 1, 0.25, 1.50, 5, 'Kablo Bağlantısı', 'WC003'),

-- Gövde Parçası BOM (Yarı mamul)
((SELECT id FROM material_cards WHERE material_code = 'M005'), (SELECT id FROM material_cards WHERE material_code = 'M001'), 0.5000, 'PRODUCTION', 1, 50.00, 25.00, 1, 'Kesim İşlemi', 'WC004'),
((SELECT id FROM material_cards WHERE material_code = 'M005'), (SELECT id FROM material_cards WHERE material_code = 'M009'), 4.0000, 'PRODUCTION', 1, 0.50, 2.00, 2, 'Delik Delme', 'WC004'),

-- Montaj Grubu BOM (Yarı mamul)
((SELECT id FROM material_cards WHERE material_code = 'M006'), (SELECT id FROM material_cards WHERE material_code = 'M003'), 1.0000, 'PRODUCTION', 1, 150.00, 150.00, 1, 'Kart Montajı', 'WC005'),
((SELECT id FROM material_cards WHERE material_code = 'M006'), (SELECT id FROM material_cards WHERE material_code = 'M004'), 1.0000, 'PRODUCTION', 1, 5.00, 5.00, 2, 'Kablo Montajı', 'WC005'),
((SELECT id FROM material_cards WHERE material_code = 'M006'), (SELECT id FROM material_cards WHERE material_code = 'M010'), 2.0000, 'PRODUCTION', 1, 0.25, 0.50, 3, 'Kablo Bağlantısı', 'WC005');

-- Örnek stok hareketleri
INSERT INTO material_stock_movements (material_id, quantity, movement_type, movement_date, reference_number, reference_type, unit_cost, total_cost, reason) VALUES
-- Hammadde girişleri
((SELECT id FROM material_cards WHERE material_code = 'M001'), 100.0000, 'PURCHASE', CURRENT_TIMESTAMP, 'PO001', 'PURCHASE', 50.00, 5000.00, 'Satın alma'),
((SELECT id FROM material_cards WHERE material_code = 'M002'), 500.0000, 'PURCHASE', CURRENT_TIMESTAMP, 'PO002', 'PURCHASE', 25.00, 12500.00, 'Satın alma'),
((SELECT id FROM material_cards WHERE material_code = 'M003'), 50.0000, 'PURCHASE', CURRENT_TIMESTAMP, 'PO003', 'PURCHASE', 150.00, 7500.00, 'Satın alma'),
((SELECT id FROM material_cards WHERE material_code = 'M004'), 1000.0000, 'PURCHASE', CURRENT_TIMESTAMP, 'PO004', 'PURCHASE', 5.00, 5000.00, 'Satın alma'),

-- Üretim çıkışları
((SELECT id FROM material_cards WHERE material_code = 'M001'), -50.0000, 'PRODUCTION_OUT', CURRENT_TIMESTAMP, 'WO001', 'PRODUCTION', 50.00, -2500.00, 'Gövde parçası üretimi'),
((SELECT id FROM material_cards WHERE material_code = 'M003'), -10.0000, 'PRODUCTION_OUT', CURRENT_TIMESTAMP, 'WO002', 'PRODUCTION', 150.00, -1500.00, 'Montaj grubu üretimi'),
((SELECT id FROM material_cards WHERE material_code = 'M004'), -10.0000, 'PRODUCTION_OUT', CURRENT_TIMESTAMP, 'WO002', 'PRODUCTION', 5.00, -50.00, 'Montaj grubu üretimi'),

-- Üretim girişleri
((SELECT id FROM material_cards WHERE material_code = 'M005'), 25.0000, 'PRODUCTION_IN', CURRENT_TIMESTAMP, 'WO001', 'PRODUCTION', 80.00, 2000.00, 'Gövde parçası üretimi'),
((SELECT id FROM material_cards WHERE material_code = 'M006'), 15.0000, 'PRODUCTION_IN', CURRENT_TIMESTAMP, 'WO002', 'PRODUCTION', 200.00, 3000.00, 'Montaj grubu üretimi'),
((SELECT id FROM material_cards WHERE material_code = 'M007'), 5.0000, 'PRODUCTION_IN', CURRENT_TIMESTAMP, 'WO003', 'PRODUCTION', 500.00, 2500.00, 'Elektrikli motor üretimi'),
((SELECT id FROM material_cards WHERE material_code = 'M008'), 3.0000, 'PRODUCTION_IN', CURRENT_TIMESTAMP, 'WO004', 'PRODUCTION', 800.00, 2400.00, 'Kontrol paneli üretimi');
