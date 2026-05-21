package com.bojan.employeeapi.dto;

import java.math.BigDecimal;
import java.util.List;

public record SalaryAnalyticsResponse(
        BigDecimal averageSalary,
        BigDecimal medianSalary,
        List<DepartmentSalaryGroupResponse> salariesByDepartment,
        List<EmployeeResponse> topFivePaidEmployees
) {
}