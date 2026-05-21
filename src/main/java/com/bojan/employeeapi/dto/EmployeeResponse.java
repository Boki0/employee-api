package com.bojan.employeeapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String department,
        BigDecimal salary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}