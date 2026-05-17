package com.fintech.transaction.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// INTERVIEW: "Double-entry ledger means every transaction has a fromAccount
// and toAccount. Money leaving one account MUST equal money entering another.
// This is how banks ensure no money is created or lost."
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_from_account", columnList = "from_account"),
    @Index(name = "idx_to_account",   columnList = "to_account"),
    @Index(name = "idx_created_at",   columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_ref", unique = true, nullable = false)
    private String transactionRef;  // e.g. "TXN-20240523-001"

    @Column(name = "from_account")
    private String fromAccount;

    @Column(name = "to_account")
    private String toAccount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;

    // AUDIT LOG — INTERVIEW TALKING POINT:
    // "Every transaction is immutable. We never UPDATE a transaction row.
    // If something fails, we insert a new FAILED/REVERSED record.
    // This gives us a complete audit trail for compliance."
    @Column(name = "initiated_by")
    private String initiatedBy;     // email of user who triggered it

    @Column(name = "fraud_flag")
    private boolean fraudFlag;      // set by fraud detection rule engine

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = TransactionStatus.PENDING;
    }

    public enum TransactionType {
        TRANSFER,   // account to account
        DEPOSIT,    // external money in
        WITHDRAWAL, // money out
        LOAN_REPAY  // loan payment
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, REVERSED
    }
}
