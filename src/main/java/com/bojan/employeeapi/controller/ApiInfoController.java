package com.bojan.employeeapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiInfoController {

    @GetMapping("/api")
    public ApiInfoResponse getApiInfo() {
        return new ApiInfoResponse(
                "Employee API",
                "Simple REST API for managing employees, salary updates, salary analytics and audit logs.",
                List.of(
                        new EndpointInfo("POST", "/api/employees", "Create a new employee"),
                        new EndpointInfo("GET", "/api/employees", "Get all employees"),
                        new EndpointInfo("GET", "/api/employees/{id}", "Get employee by ID"),
                        new EndpointInfo("GET", "/api/employees?department=QA", "Search employees by department"),
                        new EndpointInfo("PATCH", "/api/employees/{id}/salary", "Update employee salary"),
                        new EndpointInfo("GET", "/api/employees/analytics/salary", "Get salary analytics"),
                        new EndpointInfo("GET", "/api/audit-logs", "Get audit logs")
                )
        );
    }

    public record ApiInfoResponse(
            String name,
            String description,
            List<EndpointInfo> endpoints
    ) {
    }

    public record EndpointInfo(
            String method,
            String path,
            String description
    ) {
    }
}