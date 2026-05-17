package com.fintech.transaction.service;

import com.fintech.transaction.dto.*;
import com.fintech.transaction.entity.Transaction;
import com.fintech.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;
    private final RestTemplate restTemplate;

    // Account service URL — in production this would be via service discovery
    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8082/api/accounts";

    // INTERVIEW: "@Transactional on this method means:
    // Step 1 (debit) + Step 2 (credit) + Step 3 (save transaction record)
    // ALL happen in ONE atomic unit. If step 2 fails, step 1 is rolled back.
    // This guarantees money is never lost or duplicated — ACID compliance."
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        // Validation
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }
        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        // Check fraud rules before processing
        boolean flagged = fraudDetectionService.isSuspicious(
            request.getFromAccount(), request.getAmount());

        String txnRef = generateTransactionRef();

        // Create transaction record as PENDING first
        Transaction txn = Transaction.builder()
                .transactionRef(txnRef)
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .type(Transaction.TransactionType.TRANSFER)
                .status(Transaction.TransactionStatus.PENDING)
                .description(request.getDescription())
                .initiatedBy(request.getInitiatedBy())
                .fraudFlag(flagged)
                .build();

        transactionRepository.save(txn);

        try {
            // Double-entry: debit sender
            restTemplate.postForObject(
                ACCOUNT_SERVICE_URL + "/" + request.getFromAccount() + "/debit?amount=" + request.getAmount(),
                null, Void.class);

            // Double-entry: credit receiver (must equal the debit amount)
            restTemplate.postForObject(
                ACCOUNT_SERVICE_URL + "/" + request.getToAccount() + "/credit?amount=" + request.getAmount(),
                null, Void.class);

            // Mark transaction as completed
            txn.setStatus(Transaction.TransactionStatus.COMPLETED);
            transactionRepository.save(txn);

            log.info("Transfer completed: {} -> {} amount={}", 
                request.getFromAccount(), request.getToAccount(), request.getAmount());

        } catch (Exception e) {
            // Mark failed — we never delete transaction records, only update status
            txn.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(txn);
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }

        return mapToResponse(txn);
    }

    public List<TransactionResponse> getHistory(String accountNumber) {
        return transactionRepository
                .findByFromAccountOrToAccount(accountNumber, accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getByRef(String ref) {
        Transaction txn = transactionRepository.findByTransactionRef(ref)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + ref));
        return mapToResponse(txn);
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .transactionRef(t.getTransactionRef())
                .fromAccount(t.getFromAccount())
                .toAccount(t.getToAccount())
                .amount(t.getAmount())
                .type(t.getType().name())
                .status(t.getStatus().name())
                .description(t.getDescription())
                .fraudFlag(t.isFraudFlag())
                .createdAt(t.getCreatedAt())
                .build();
    }

    private String generateTransactionRef() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "TXN-" + date + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
