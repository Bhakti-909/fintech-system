package com.fintech.loan.controller;
import com.fintech.loan.entity.Loan;
import com.fintech.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<Loan> apply(@RequestBody Map<String,String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            loanService.applyForLoan(
                body.get("email"), body.get("accountNumber"),
                new BigDecimal(body.get("principal")),
                new BigDecimal(body.get("annualRate")),
                Integer.parseInt(body.get("tenureMonths"))
            ));
    }

    @PutMapping("/{loanRef}/approve")
    public ResponseEntity<Loan> approve(@PathVariable String loanRef) {
        return ResponseEntity.ok(loanService.approveLoan(loanRef));
    }

    @PutMapping("/{loanRef}/repay")
    public ResponseEntity<Loan> repay(@PathVariable String loanRef) {
        return ResponseEntity.ok(loanService.repayEmi(loanRef));
    }
}
