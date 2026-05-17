package com.fintech.transaction.repository;

import com.fintech.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccount(String from, String to);

    Optional<Transaction> findByTransactionRef(String ref);

    // INTERVIEW: "I added a @Query to find suspicious transactions —
    // large amounts in a short window. This is rule-based fraud detection."
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromAccount = :account OR t.toAccount = :account) " +
           "AND t.createdAt >= :since " +
           "AND t.amount >= :threshold")
    List<Transaction> findLargeRecentTransactions(String account,
                                                   LocalDateTime since,
                                                   BigDecimal threshold);

    // Count transactions per account in last N minutes — for rate limiting
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromAccount = :account " +
           "AND t.createdAt >= :since")
    long countRecentOutgoing(String account, LocalDateTime since);
}
