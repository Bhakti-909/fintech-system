package com.fintech.auth.repository;

import com.fintech.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// INTERVIEW: "JpaRepository gives us save(), findById(), findAll(), delete()
// for free. Spring generates the SQL at runtime — no boilerplate needed.
// Custom queries like findByEmail are auto-generated from the method name."
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring generates: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Spring generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
}
