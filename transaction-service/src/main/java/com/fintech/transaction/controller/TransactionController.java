package com.fintech.transaction.controller;

import com.fintech.transaction.dto.*;
import com.fintech.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST /api/transactions/transfer
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transfer(request));
    }

    // GET /api/transactions/history/{accountNumber}
    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getHistory(accountNumber));
    }

    // GET /api/transactions/{ref}
    @GetMapping("/{ref}")
    public ResponseEntity<TransactionResponse> getByRef(@PathVariable String ref) {
        return ResponseEntity.ok(transactionService.getByRef(ref));
    }
}
