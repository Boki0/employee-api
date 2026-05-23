package com.bojan.employeeapi.service;

import com.bojan.employeeapi.dto.CreateEmployeeRequest;
import com.bojan.employeeapi.dto.DepartmentSalaryGroupResponse;
import com.bojan.employeeapi.dto.EmployeeResponse;
import com.bojan.employeeapi.dto.SalaryAnalyticsResponse;
import com.bojan.employeeapi.dto.UpdateSalaryRequest;
import com.bojan.employeeapi.exception.ConflictException;
import com.bojan.employeeapi.exception.ResourceNotFoundException;
import com.bojan.employeeapi.model.AuditAction;
import com.bojan.employeeapi.model.Employee;
import com.bojan.employeeapi.repository.EmployeeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            AuditService auditService,
            EmployeeMapper employeeMapper
    ) {
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
        this.employeeMapper = employeeMapper;
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        String employeeCode = request.employeeCode().trim();

        if (employeeRepository.existsByEmployeeCode(employeeCode)) {
            throw new ConflictException("Employee with code '" + employeeCode + "' already exists.");
        }

        Employee employee = new Employee(
                employeeCode,
                request.firstName().trim(),
                request.lastName().trim(),
                request.department().trim(),
                request.salary()
        );

        try {
            Employee savedEmployee = employeeRepository.saveAndFlush(employee);

            auditService.logEmployeeAction(
                    AuditAction.EMPLOYEE_CREATED,
                    savedEmployee,
                    null,
                    savedEmployee.getSalary(),
                    "Employee created"
            );

            return employeeMapper.toResponse(savedEmployee);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Employee with code '" + employeeCode + "' already exists.");
        }
    }

    @Transactional
    public List<EmployeeResponse> searchEmployees(String department, String query) {
        List<Employee> employees = employeeRepository.findAll();

        String departmentFilter = department == null ? null : department.trim().toLowerCase();
        String queryFilter = query == null ? null : query.trim().toLowerCase();

        List<Employee> filteredEmployees = employees.stream()
                .filter(employee -> departmentFilter == null || departmentFilter.isBlank()
                        || employee.getDepartment().toLowerCase().equals(departmentFilter))
                .filter(employee -> queryFilter == null || queryFilter.isBlank()
                        || employee.getFirstName().toLowerCase().contains(queryFilter)
                        || employee.getLastName().toLowerCase().contains(queryFilter)
                        || employee.getEmployeeCode().toLowerCase().contains(queryFilter))
                .toList();

        auditService.logSystemAction(
                AuditAction.EMPLOYEES_SEARCHED,
                "Employees searched. department=" + department + ", query=" + query + ", resultCount=" + filteredEmployees.size()
        );

        return filteredEmployees.stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployeeById(id);

        auditService.logEmployeeAction(
                AuditAction.EMPLOYEE_DETAILS_FETCHED,
                employee,
                null,
                employee.getSalary(),
                "Employee details fetched"
        );

        return employeeMapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateSalary(Long id, UpdateSalaryRequest request) {
        Employee employee = findEmployeeById(id);

        BigDecimal oldSalary = employee.getSalary();
        BigDecimal newSalary = request.newSalary();

        employee.setSalary(newSalary);

        Employee updatedEmployee = employeeRepository.saveAndFlush(employee);

        auditService.logEmployeeAction(
                AuditAction.SALARY_UPDATED,
                updatedEmployee,
                oldSalary,
                newSalary,
                "Salary changed from " + oldSalary + " to " + newSalary
        );

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public SalaryAnalyticsResponse getSalaryAnalytics() {
        List<Employee> employees = employeeRepository.findAll();

        BigDecimal averageSalary = calculateAverageSalary(employees);
        BigDecimal medianSalary = calculateMedianSalary(employees);

        List<DepartmentSalaryGroupResponse> salariesByDepartment = calculateSalariesByDepartment(employees);

        List<EmployeeResponse> topFivePaidEmployees = employeeRepository.findTop5ByOrderBySalaryDesc()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();

        auditService.logSystemAction(
                AuditAction.SALARY_ANALYTICS_REQUESTED,
                "Salary analytics requested"
        );

        return new SalaryAnalyticsResponse(
                averageSalary,
                medianSalary,
                salariesByDepartment,
                topFivePaidEmployees
        );
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee with id " + id + " was not found.")
                );
    }

    private BigDecimal calculateAverageSalary(List<Employee> employees) {
        if (employees.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = employees.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(employees.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMedianSalary(List<Employee> employees) {
        if (employees.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> sortedSalaries = employees.stream()
                .map(Employee::getSalary)
                .sorted()
                .toList();

        int size = sortedSalaries.size();
        int middle = size / 2;

        if (size % 2 == 1) {
            return sortedSalaries.get(middle);
        }

        return sortedSalaries.get(middle - 1)
                .add(sortedSalaries.get(middle))
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    private List<DepartmentSalaryGroupResponse> calculateSalariesByDepartment(List<Employee> employees) {
        Map<String, List<Employee>> employeesByDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));

        return employeesByDepartment.entrySet()
                .stream()
                .map(entry -> {
                    String department = entry.getKey();
                    List<Employee> departmentEmployees = entry.getValue();

                    BigDecimal totalSalary = departmentEmployees.stream()
                            .map(Employee::getSalary)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal averageSalary = totalSalary.divide(
                            BigDecimal.valueOf(departmentEmployees.size()),
                            2,
                            RoundingMode.HALF_UP
                    );

                    return new DepartmentSalaryGroupResponse(
                            department,
                            averageSalary,
                            totalSalary,
                            (long) departmentEmployees.size()
                    );
                })
                .sorted(Comparator.comparing(DepartmentSalaryGroupResponse::department))
                .toList();
    }
}