package com.bojan.employeeapi.dto;

import java.math.BigDecimal;

public record DepartmentSalaryGroupResponse(
        String department,
        BigDecimal averageSalary,
        BigDecimal totalSalary,
        Long employeeCount
) {
}