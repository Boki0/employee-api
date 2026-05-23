package com.bojan.employeeapi.controller;

import com.bojan.employeeapi.dto.CreateEmployeeRequest;
import com.bojan.employeeapi.dto.EmployeeResponse;
import com.bojan.employeeapi.dto.SalaryAnalyticsResponse;
import com.bojan.employeeapi.dto.UpdateSalaryRequest;
import com.bojan.employeeapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @GetMapping
    public List<EmployeeResponse> searchEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String query
    ) {
        return employeeService.searchEmployees(department, query);
    }

    @GetMapping("/analytics/salary")
    public SalaryAnalyticsResponse getSalaryAnalytics() {
        return employeeService.getSalaryAnalytics();
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PatchMapping("/{id}/salary")
    public EmployeeResponse updateSalary(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSalaryRequest request
    ) {
        return employeeService.updateSalary(id, request);
    }
}