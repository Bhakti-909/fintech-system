package com.fintech.loan.service;

import com.fintech.loan.entity.Loan;
import com.fintech.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    // INTERVIEW: "EMI formula: EMI = P * r * (1+r)^n / ((1+r)^n - 1)
    // P = principal, r = monthly rate, n = tenure in months.
    // I used BigDecimal with MathContext.DECIMAL128 for high-precision finance math."
    public Loan applyForLoan(String email, String accountNumber,
                              BigDecimal principal, BigDecimal annualRate, int tenureMonths) {

        BigDecimal monthlyRate = annualRate
                .divide(new BigDecimal("1200"), MathContext.DECIMAL128); // annual% / 12 / 100

        // (1+r)^n
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusR.pow(tenureMonths, MathContext.DECIMAL128);

        // EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal emi = principal
                .multiply(monthlyRate)
                .multiply(power)
                .divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        String loanRef = "LOAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Loan loan = Loan.builder()
                .loanRef(loanRef)
                .applicantEmail(email)
                .accountNumber(accountNumber)
                .principal(principal)
                .interestRate(annualRate)
                .tenureMonths(tenureMonths)
                .emiAmount(emi)
                .outstandingBalance(principal)
                .status(Loan.LoanStatus.PENDING)
                .build();

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan approveLoan(String loanRef) {
        Loan loan = loanRepository.findByLoanRef(loanRef)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanRef));

        if (loan.getStatus() != Loan.LoanStatus.PENDING) {
            throw new RuntimeException("Loan is not in PENDING state");
        }

        loan.setStatus(Loan.LoanStatus.APPROVED);
        loan.setDisbursedOn(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusMonths(loan.getTenureMonths()));

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan repayEmi(String loanRef) {
        Loan loan = loanRepository.findByLoanRef(loanRef)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active");
        }

        BigDecimal newBalance = loan.getOutstandingBalance().subtract(loan.getEmiAmount());

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setOutstandingBalance(BigDecimal.ZERO);
            loan.setStatus(Loan.LoanStatus.CLOSED);
        } else {
            loan.setOutstandingBalance(newBalance);
        }

        return loanRepository.save(loan);
    }
}
