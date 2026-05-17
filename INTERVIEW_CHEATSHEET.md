# Fintech System — Interview Talking Points Cheat Sheet

## Architecture Overview (say this first)
"I built a microservices-based fintech backend with 5 independent services:
Auth, Account, Transaction, Loan, and Notification.
Each service has its own database (database-per-service pattern), runs in its own
Docker container, and communicates with other services via REST APIs."

---

## Service-by-Service Explanation

### Auth Service (port 8081)
- Handles user registration and login
- Passwords stored as BCrypt hashes — never plain text
- Issues JWT tokens on successful login
- JWT = Header.Payload.Signature — validated on every request via JwtFilter
- Spring Security configured as STATELESS — no sessions

**Q: Why stateless?**
"Because any of our service instances can validate a JWT without sharing session storage.
This is critical for horizontal scaling in microservices."

### Account Service (port 8082)
- Creates and manages bank accounts
- Balance stored as BigDecimal — never double/float (floating point rounding errors in money!)
- @Version annotation = optimistic locking, prevents concurrent balance corruption
- credit() and debit() methods are @Transactional

**Q: What is @Transactional?**
"It wraps the method in a single DB transaction. If anything fails, the entire operation
rolls back. Without it, a crash between debit and credit would lose money."

### Transaction Service (port 8083)
- Implements double-entry ledger accounting
- Every transfer: debit sender EXACTLY = credit receiver
- Transactions are immutable — we never UPDATE, only INSERT new records
- FraudDetectionService runs 3 rules: high value, velocity check, unusual hours
- @Transactional ensures debit + credit + record save are atomic

**Q: What is double-entry ledger?**
"Money leaving one account must exactly equal money entering another.
No money is created or destroyed. It's how every real bank works."

### Loan Service (port 8084)
- Apply for loan, approve, and track EMI repayments
- EMI formula: P × r × (1+r)^n / ((1+r)^n - 1)
  where P = principal, r = monthly interest rate, n = tenure in months
- Used BigDecimal with MathContext.DECIMAL128 for precision

### Notification Service (port 8085)
- Receives events from other services (transfer, login, fraud alert)
- Currently logs notifications — production ready to swap for JavaMail/Twilio/Kafka consumer
- Event-driven design: services publish events, notification service reacts

---

## Key Design Decisions (be ready for "why did you...")

### Why microservices instead of monolith?
"Each service can be scaled, deployed, and updated independently.
If the transaction service has high load, I scale only that.
In a monolith I'd scale everything even if only one feature is busy."

### Why 5 services specifically?
"I divided by business domain — each service owns one bounded context:
Auth owns users/tokens, Account owns balances, Transaction owns money movement,
Loan owns credit products, Notification owns communication.
This follows Domain-Driven Design principles."

### Why PostgreSQL?
"Financial data needs ACID guarantees — Atomicity, Consistency, Isolation, Durability.
PostgreSQL is ACID-compliant. NoSQL databases like MongoDB sacrifice consistency
for speed, which is unacceptable for banking."

### Why JWT over sessions?
"JWTs are stateless — no server-side storage needed.
Any microservice instance can validate a JWT using the shared secret.
Sessions would require a shared session store (like Redis) across services."

### How does fraud detection work?
"Rule-based engine with 3 rules:
1. Amount > 50,000 → flagged (high value)
2. More than 10 transactions in 1 hour from same account → flagged (velocity)
3. Transaction between 1am–4am → flagged (unusual time)
Flagged transactions are saved with fraudFlag=true for manual review."

---

## SOLID Principles in the project
- **S**ingle Responsibility: FraudDetectionService only detects fraud, TransactionService only processes transfers
- **O**pen/Closed: Adding a new fraud rule = new method, not modifying existing ones
- **L**iskov: Not directly applicable but interfaces (JpaRepository) are swappable
- **I**nterface Segregation: Repository interfaces are small and focused
- **D**ependency Inversion: Services depend on repository interfaces, not concrete classes

---

## Common Interview Questions

**Q: What is N+1 problem?**
"If you fetch 100 accounts and then load each account's transactions in a loop,
you make 1 + 100 = 101 queries. Fix: use @Query with JOIN FETCH or pagination."

**Q: What is indexing?**
"I added @Index on from_account, to_account, and created_at in the Transaction table.
Without indexes, PostgreSQL scans every row to find matching records — O(n).
With indexes, it's O(log n). Critical for transaction history queries."

**Q: What is ACID?**
"Atomicity: all or nothing. Consistency: data always valid. Isolation: concurrent
transactions don't interfere. Durability: committed data survives crashes."

**Q: Difference between @Component, @Service, @Repository?**
"All three register beans in Spring context. @Service signals business logic layer.
@Repository adds exception translation (DB exceptions → Spring DataAccessException).
@Component is generic — use the others when you can for clarity."

---

## Running the project
```
# Start everything
docker-compose up --build

# Test register
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"bhakti@test.com","password":"Pass@123","role":"CUSTOMER"}'

# Test login (get JWT)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"bhakti@test.com","password":"Pass@123"}'

# Create account (use token from login)
curl -X POST http://localhost:8082/api/accounts \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"ownerEmail":"bhakti@test.com","accountType":"SAVINGS"}'

# Transfer money
curl -X POST http://localhost:8083/api/transactions/transfer \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"fromAccount":"ACC-123456","toAccount":"ACC-789012","amount":5000,"description":"Rent"}'
```
