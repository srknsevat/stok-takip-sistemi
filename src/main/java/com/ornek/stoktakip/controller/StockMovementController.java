package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stock-movements")
public class StockMovementController {
    private final StockMovementService stockMovementService;

    @Autowired
    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public String listMovements(Model model) {
        model.addAttribute("movements", stockMovementService.getAllMovements());
        return "stock-movements/list";
    }
} 