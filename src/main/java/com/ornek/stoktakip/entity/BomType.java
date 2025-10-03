package com.ornek.stoktakip.entity;

public enum BomType {
    STANDARD("STANDARD", "Standart BOM"),
    ENGINEERING("ENGINEERING", "Mühendislik BOM"),
    MANUFACTURING("MANUFACTURING", "Üretim BOM"),
    SALES("SALES", "Satış BOM"),
    COSTING("COSTING", "Maliyet BOM"),
    PLANNING("PLANNING", "Planlama BOM"),
    PHANTOM("PHANTOM", "Hayalet BOM"),
    CONFIGURABLE("CONFIGURABLE", "Yapılandırılabilir BOM");
    
    private final String code;
    private final String description;
    
    BomType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static BomType fromCode(String code) {
        for (BomType type : BomType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Geçersiz BOM tipi: " + code);
    }
}
