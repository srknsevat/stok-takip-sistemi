package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.MaterialCard;

import java.math.BigDecimal;
import java.util.Map;

public interface BomExplosionService {
	/**
	 * Verilen ana malzemenin BOM'unu patlatır ve birim (1) üretim için alt parça gereksinimlerini döner.
	 */
	Map<String, BomExplosionResult> explodeBOM(Long parentMaterialId, BigDecimal parentQuantity);

	class BomExplosionResult {
		private final MaterialCard material;
		private final BigDecimal requiredQuantity;
		private final Integer bomLevel;
		private final String bomPath;

		public BomExplosionResult(MaterialCard material, BigDecimal requiredQuantity, Integer bomLevel, String bomPath) {
			this.material = material;
			this.requiredQuantity = requiredQuantity;
			this.bomLevel = bomLevel;
			this.bomPath = bomPath;
		}

		public MaterialCard getMaterial() { return material; }
		public BigDecimal getRequiredQuantity() { return requiredQuantity; }
		public Integer getBomLevel() { return bomLevel; }
		public String getBomPath() { return bomPath; }
	}
}
