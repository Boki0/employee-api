package com.bojan.employeeapi.controller;

import com.bojan.employeeapi.dto.AuditLogResponse;
import com.bojan.employeeapi.service.AuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditService.getAllAuditLogs();
    }
}