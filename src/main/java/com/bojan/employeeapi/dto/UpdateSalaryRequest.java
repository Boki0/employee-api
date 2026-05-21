package com.bojan.employeeapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateSalaryRequest(

        @NotNull(message = "New salary is required")
        @Positive(message = "New salary must be greater than zero")
        BigDecimal newSalary
) {
}