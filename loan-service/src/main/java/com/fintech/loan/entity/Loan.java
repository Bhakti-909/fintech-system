package com.fintech.loan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_ref", unique = true, nullable = false)
    private String loanRef;

    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principal;       // original loan amount

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;    // annual interest rate %

    @Column(name = "tenure_months", nullable = false)
    private int tenureMonths;

    @Column(name = "emi_amount", precision = 19, scale = 4)
    private BigDecimal emiAmount;       // computed monthly EMI

    @Column(name = "outstanding_balance", precision = 19, scale = 4)
    private BigDecimal outstandingBalance;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "disbursed_on")
    private LocalDate disbursedOn;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = LoanStatus.PENDING;
    }

    public enum LoanStatus {
        PENDING, APPROVED, ACTIVE, CLOSED, DEFAULTED
    }
}
