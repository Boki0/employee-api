package com.bojan.employeeapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateEmployeeRequest(

        @NotBlank(message = "Employee code is required")
        String employeeCode,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Department is required")
        String department,

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than zero")
        BigDecimal salary
) {
}