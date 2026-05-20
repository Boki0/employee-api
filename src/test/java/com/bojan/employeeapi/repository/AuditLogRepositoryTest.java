package com.bojan.employeeapi.repository;

import com.bojan.employeeapi.model.AuditAction;
import com.bojan.employeeapi.model.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    @DisplayName("Should save audit log for salary update")
    void shouldSaveAuditLogForSalaryUpdate() {
        AuditLog auditLog = new AuditLog(
                AuditAction.SALARY_UPDATED,
                1L,
                "EMP-001",
                "Pera Peric",
                "Engineering",
                new BigDecimal("1500.00"),
                new BigDecimal("1800.00"),
                "Salary changed from 1500.00 to 1800.00"
        );

        AuditLog savedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        assertThat(savedAuditLog.getId()).isNotNull();
        assertThat(savedAuditLog.getAction()).isEqualTo(AuditAction.SALARY_UPDATED);
        assertThat(savedAuditLog.getOldSalary()).isEqualByComparingTo("1500.00");
        assertThat(savedAuditLog.getNewSalary()).isEqualByComparingTo("1800.00");
        assertThat(savedAuditLog.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should return audit logs ordered by newest first")
    void shouldReturnAuditLogsOrderedByNewestFirst() throws InterruptedException {
        AuditLog firstLog = new AuditLog(
                AuditAction.EMPLOYEE_CREATED,
                1L,
                "EMP-001",
                "Pera Peric",
                "Engineering",
                null,
                new BigDecimal("1500.00"),
                "Employee created"
        );

        auditLogRepository.saveAndFlush(firstLog);

        Thread.sleep(10);

        AuditLog secondLog = new AuditLog(
                AuditAction.SALARY_ANALYTICS_REQUESTED,
                null,
                null,
                null,
                null,
                null,
                null,
                "Salary analytics requested"
        );

        auditLogRepository.saveAndFlush(secondLog);

        List<AuditLog> logs = auditLogRepository.findAllByOrderByCreatedAtDesc();

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getAction()).isEqualTo(AuditAction.SALARY_ANALYTICS_REQUESTED);
        assertThat(logs.get(1).getAction()).isEqualTo(AuditAction.EMPLOYEE_CREATED);
    }
}