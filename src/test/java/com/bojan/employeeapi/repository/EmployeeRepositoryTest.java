package com.bojan.employeeapi.repository;

import com.bojan.employeeapi.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Should save employee and find it by employee code")
    void shouldSaveEmployeeAndFindByEmployeeCode() {
        Employee employee = new Employee(
                "EMP-001",
                "Pera",
                "Peric",
                "Engineering",
                new BigDecimal("1500.00")
        );

        employeeRepository.save(employee);

        Optional<Employee> result = employeeRepository.findByEmployeeCode("EMP-001");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Pera");
        assertThat(result.get().getLastName()).isEqualTo("Peric");
        assertThat(result.get().getDepartment()).isEqualTo("Engineering");
        assertThat(result.get().getSalary()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("Should return true when employee code already exists")
    void shouldReturnTrueWhenEmployeeCodeExists() {
        Employee employee = new Employee(
                "EMP-002",
                "Mika",
                "Mikic",
                "QA",
                new BigDecimal("1200.00")
        );

        employeeRepository.save(employee);

        boolean exists = employeeRepository.existsByEmployeeCode("EMP-002");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should prevent duplicate employee code")
    void shouldPreventDuplicateEmployeeCode() {
        Employee firstEmployee = new Employee(
                "EMP-003",
                "Ana",
                "Anic",
                "HR",
                new BigDecimal("1300.00")
        );

        Employee duplicateEmployee = new Employee(
                "EMP-003",
                "Jovan",
                "Jovanovic",
                "Finance",
                new BigDecimal("1600.00")
        );

        employeeRepository.saveAndFlush(firstEmployee);

        assertThatThrownBy(() -> employeeRepository.saveAndFlush(duplicateEmployee))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should find employees by department ignoring case")
    void shouldFindEmployeesByDepartmentIgnoringCase() {
        employeeRepository.save(new Employee(
                "EMP-004",
                "Marko",
                "Markovic",
                "Engineering",
                new BigDecimal("2000.00")
        ));

        employeeRepository.save(new Employee(
                "EMP-005",
                "Nikola",
                "Nikolic",
                "Engineering",
                new BigDecimal("2200.00")
        ));

        List<Employee> employees = employeeRepository.findByDepartmentIgnoreCase("engineering");

        assertThat(employees).hasSize(2);
    }

    @Test
    @DisplayName("Should automatically set createdAt and updatedAt")
    void shouldAutomaticallySetCreatedAtAndUpdatedAt() {
        Employee employee = new Employee(
                "EMP-006",
                "Sara",
                "Saric",
                "Marketing",
                new BigDecimal("1100.00")
        );

        Employee savedEmployee = employeeRepository.saveAndFlush(employee);

        assertThat(savedEmployee.getCreatedAt()).isNotNull();
        assertThat(savedEmployee.getUpdatedAt()).isNotNull();
    }
}