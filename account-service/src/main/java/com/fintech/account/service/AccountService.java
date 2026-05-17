package com.fintech.account.service;

import com.fintech.account.dto.*;
import com.fintech.account.entity.Account;
import com.fintech.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {
        String accNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accNumber)
                .ownerEmail(request.getOwnerEmail())
                .accountType(Account.AccountType.valueOf(request.getAccountType().toUpperCase()))
                .balance(BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return mapToResponse(account);
    }

    public List<AccountResponse> getAccountsByEmail(String email) {
        return accountRepository.findByOwnerEmail(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // INTERVIEW: "@Transactional ensures the entire method runs in ONE DB transaction.
    // If anything fails midway, the entire operation rolls back.
    // This is ACID compliance — critical for financial operations."
    @Transactional
    public void credit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    @Transactional
    public void debit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // INTERVIEW: "compareTo is correct for BigDecimal comparison.
        // Never use == or .equals() to compare BigDecimal values."
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .ownerEmail(account.getOwnerEmail())
                .balance(account.getBalance())
                .accountType(account.getAccountType().name())
                .status(account.getStatus().name())
                .build();
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "ACC-" + (100000 + new Random().nextInt(900000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
