package com.bojan.employeeapi.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private AuditAction action;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "employee_full_name", length = 250)
    private String employeeFullName;

    @Column(length = 100)
    private String department;

    @Column(name = "old_salary", precision = 12, scale = 2)
    private BigDecimal oldSalary;

    @Column(name = "new_salary", precision = 12, scale = 2)
    private BigDecimal newSalary;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(
            AuditAction action,
            Long employeeId,
            String employeeCode,
            String employeeFullName,
            String department,
            BigDecimal oldSalary,
            BigDecimal newSalary,
            String details
    ) {
        this.action = action;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeFullName = employeeFullName;
        this.department = department;
        this.oldSalary = oldSalary;
        this.newSalary = newSalary;
        this.details = details;
    }

    @PrePersist
    public void beforeInsert() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AuditAction getAction() {
        return action;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public String getDepartment() {
        return department;
    }

    public BigDecimal getOldSalary() {
        return oldSalary;
    }

    public BigDecimal getNewSalary() {
        return newSalary;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}