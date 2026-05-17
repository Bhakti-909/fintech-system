package com.fintech.account.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// INTERVIEW: "I used BigDecimal for money — never use double or float for
// financial calculations. Floating point causes rounding errors.
// BigDecimal gives exact decimal arithmetic."
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;   // e.g. "ACC-1001"

    @Column(name = "owner_email", nullable = false)
    private String ownerEmail;      // links to User in auth-service (no FK across services!)

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;     // exact decimal, 4 decimal places

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Version  // Optimistic locking — prevents concurrent balance updates corrupting data
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.balance == null) this.balance = BigDecimal.ZERO;
        if (this.status == null) this.status = AccountStatus.ACTIVE;
    }

    public enum AccountType { SAVINGS, CURRENT, LOAN }
    public enum AccountStatus { ACTIVE, SUSPENDED, CLOSED }
}
