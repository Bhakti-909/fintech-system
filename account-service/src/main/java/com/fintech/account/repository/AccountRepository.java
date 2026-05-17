package com.fintech.account.repository;

import com.fintech.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByOwnerEmail(String ownerEmail);

    boolean existsByAccountNumber(String accountNumber);

    // JPQL query - runs on any JPA-supported DB
    @Query("SELECT a FROM Account a WHERE a.ownerEmail = :email AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByEmail(String email);
}
