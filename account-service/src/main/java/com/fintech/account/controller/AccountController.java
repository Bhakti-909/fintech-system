package com.fintech.account.controller;

import com.fintech.account.dto.*;
import com.fintech.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // POST /api/accounts
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    // GET /api/accounts/{accountNumber}
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    // GET /api/accounts/user/{email}
    @GetMapping("/user/{email}")
    public ResponseEntity<List<AccountResponse>> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(accountService.getAccountsByEmail(email));
    }
}
