package com.bojan.employeeapi.dto;

import com.bojan.employeeapi.model.AuditAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        AuditAction action,
        Long employeeId,
        String employeeCode,
        String employeeFullName,
        String department,
        BigDecimal oldSalary,
        BigDecimal newSalary,
        String details,
        LocalDateTime createdAt
) {
}