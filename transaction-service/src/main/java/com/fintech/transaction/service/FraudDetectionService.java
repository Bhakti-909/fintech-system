package com.fintech.transaction.service;

import com.fintech.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// INTERVIEW: "This is our rule-based fraud detection engine.
// I didn't use an ML library — I built weighted rules that check:
// 1. Is the amount unusually large? (> 50,000)
// 2. Too many transactions in a short window? (rate limiting)
// 3. Transactions at unusual hours?
// If any rule fires, the transaction is flagged for review.
// This is similar to how early fraud systems worked at banks."
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("50000");
    private static final int MAX_TXN_PER_HOUR = 10;

    public boolean isSuspicious(String fromAccount, BigDecimal amount) {
        boolean suspicious = false;

        // Rule 1: High value transaction
        if (amount.compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            log.warn("FRAUD RULE 1 TRIGGERED: High value txn {} from {}", amount, fromAccount);
            suspicious = true;
        }

        // Rule 2: Too many transactions in last hour (velocity check)
        long recentCount = transactionRepository.countRecentOutgoing(
            fromAccount, LocalDateTime.now().minusHours(1));
        if (recentCount >= MAX_TXN_PER_HOUR) {
            log.warn("FRAUD RULE 2 TRIGGERED: {} txns in last hour from {}", recentCount, fromAccount);
            suspicious = true;
        }

        // Rule 3: Unusual hour (between 1am and 4am)
        int hour = LocalDateTime.now().getHour();
        if (hour >= 1 && hour <= 4) {
            log.warn("FRAUD RULE 3 TRIGGERED: Transaction at unusual hour {} from {}", hour, fromAccount);
            suspicious = true;
        }

        return suspicious;
    }
}
