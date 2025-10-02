package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.AuditLog;
import com.ornek.stoktakip.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/audit-logs")
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    @Autowired
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }
    
    @GetMapping
    public String auditLogList(Model model) {
        List<AuditLog> auditLogs = auditLogService.getRecentAuditLogs(100);
        model.addAttribute("auditLogs", auditLogs);
        return "audit-logs/list";
    }
    
    @GetMapping("/user/{userId}")
    public String auditLogsByUser(@PathVariable Long userId, Model model) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByUser(userId);
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("userId", userId);
        return "audit-logs/user-logs";
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    public String auditLogsByEntity(@PathVariable String entityType, @PathVariable Long entityId, Model model) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByEntity(entityType, entityId);
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("entityType", entityType);
        model.addAttribute("entityId", entityId);
        return "audit-logs/entity-logs";
    }
    
    @GetMapping("/action/{action}")
    public String auditLogsByAction(@PathVariable String action, Model model) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByAction(action);
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("action", action);
        return "audit-logs/action-logs";
    }
    
    // API Endpoints
    @GetMapping("/api/recent")
    @ResponseBody
    public ResponseEntity<List<AuditLog>> getRecentAuditLogsApi(@RequestParam(defaultValue = "50") int limit) {
        List<AuditLog> auditLogs = auditLogService.getRecentAuditLogs(limit);
        return ResponseEntity.ok(auditLogs);
    }
    
    @GetMapping("/api/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<AuditLog>> getAuditLogsByUserApi(@PathVariable Long userId) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByUser(userId);
        return ResponseEntity.ok(auditLogs);
    }
    
    @GetMapping("/api/entity/{entityType}/{entityId}")
    @ResponseBody
    public ResponseEntity<List<AuditLog>> getAuditLogsByEntityApi(@PathVariable String entityType, @PathVariable Long entityId) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByEntity(entityType, entityId);
        return ResponseEntity.ok(auditLogs);
    }
    
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAuditLogStatsApi() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogs", auditLogService.getAuditLogCount());
        stats.put("recentLogs", auditLogService.getRecentAuditLogs(10).size());
        return ResponseEntity.ok(stats);
    }
}
