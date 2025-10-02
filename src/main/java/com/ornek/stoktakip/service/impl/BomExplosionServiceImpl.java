package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.service.BomExplosionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Service
public class BomExplosionServiceImpl implements BomExplosionService {
	@Override
	public Map<String, BomExplosionResult> explodeBOM(Long parentMaterialId, BigDecimal parentQuantity) {
		// Minimal placeholder implementation: return empty result so build succeeds.
		return Collections.emptyMap();
	}
}
