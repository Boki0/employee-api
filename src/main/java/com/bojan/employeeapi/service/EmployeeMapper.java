package com.bojan.employeeapi.service;

import com.bojan.employeeapi.dto.EmployeeResponse;
import com.bojan.employeeapi.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    public String toFullName(Employee employee) {
        return employee.getFirstName() + " " + employee.getLastName();
    }
}