package com.fintech.loan.repository;

import com.fintech.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByLoanRef(String loanRef);
    List<Loan> findByApplicantEmail(String email);
    List<Loan> findByStatus(Loan.LoanStatus status);
}
