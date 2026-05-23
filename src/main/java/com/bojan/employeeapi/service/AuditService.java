package com.bojan.employeeapi.service;

import com.bojan.employeeapi.dto.AuditLogResponse;
import com.bojan.employeeapi.model.AuditAction;
import com.bojan.employeeapi.model.AuditLog;
import com.bojan.employeeapi.model.Employee;
import com.bojan.employeeapi.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final EmployeeMapper employeeMapper;

    public AuditService(AuditLogRepository auditLogRepository, EmployeeMapper employeeMapper) {
        this.auditLogRepository = auditLogRepository;
        this.employeeMapper = employeeMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEmployeeAction(
            AuditAction action,
            Employee employee,
            BigDecimal oldSalary,
            BigDecimal newSalary,
            String details
    ) {
        AuditLog auditLog = new AuditLog(
                action,
                employee.getId(),
                employee.getEmployeeCode(),
                employeeMapper.toFullName(employee),
                employee.getDepartment(),
                oldSalary,
                newSalary,
                details
        );

        auditLogRepository.saveAndFlush(auditLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSystemAction(AuditAction action, String details) {
        AuditLog auditLog = new AuditLog(
                action,
                null,
                null,
                null,
                null,
                null,
                null,
                details
        );

        auditLogRepository.saveAndFlush(auditLog);
    }

    @Transactional
    public List<AuditLogResponse> getAllAuditLogs() {
        logSystemAction(AuditAction.AUDIT_LOGS_FETCHED, "Audit logs fetched");

        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getAction(),
                auditLog.getEmployeeId(),
                auditLog.getEmployeeCode(),
                auditLog.getEmployeeFullName(),
                auditLog.getDepartment(),
                auditLog.getOldSalary(),
                auditLog.getNewSalary(),
                auditLog.getDetails(),
                auditLog.getCreatedAt()
        );
    }
}