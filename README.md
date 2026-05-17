# Fintech System — Banking & Financial Management Platform

> A production-grade microservices backend for core banking operations, built with Java, Spring Boot, PostgreSQL, and Docker.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)
![JWT](https://img.shields.io/badge/Auth-JWT-black?style=flat-square&logo=jsonwebtokens)

---

## Overview

Fintech System is a **microservices-based banking backend** consisting of 5 independent services that handle the complete lifecycle of banking operations — from user authentication and account management to fund transfers, loan processing, and real-time notifications.

Key engineering highlights:
- **Double-entry ledger accounting** ensuring zero money loss during transfers
- **Rule-based fraud detection** engine with velocity checks and anomaly flags
- **JWT + RBAC** security across all services with stateless authentication
- **Optimistic locking** (`@Version`) preventing concurrent balance corruption
- **BigDecimal** precision for all financial calculations — no floating point errors
- **Database-per-service** pattern for true microservice independence

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Frontend Dashboard                   │
│              (HTML + Vanilla JS — port: file://)         │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTP REST
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼─────┐     ┌──────▼──────┐    ┌──────▼──────┐
   │  Auth    │     │   Account   │    │ Transaction  │
   │ Service  │     │   Service   │    │   Service    │
   │ :8081    │     │   :8082     │    │   :8083      │
   └────┬─────┘     └──────┬──────┘    └──────┬──────┘
        │                  │                  │
   ┌────▼──────────────────▼──────────────────▼──────┐
   │                  PostgreSQL                      │
   │  fintech_auth | fintech_accounts | fintech_txns  │
   └──────────────────────────────────────────────────┘

   ┌─────────────┐     ┌──────────────────┐
   │    Loan     │     │  Notification    │
   │   Service   │     │    Service       │
   │   :8084     │     │    :8085         │
   └─────────────┘     └──────────────────┘
```

---

## Services

| Service | Port | Responsibility |
|---------|------|----------------|
| **auth-service** | 8081 | User registration, login, JWT token generation |
| **account-service** | 8082 | Account creation, balance management, credit/debit |
| **transaction-service** | 8083 | Fund transfers, double-entry ledger, fraud detection |
| **loan-service** | 8084 | Loan applications, EMI calculation, repayment tracking |
| **notification-service** | 8085 | Transfer alerts, login notifications, fraud alerts |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Persistence | Spring Data JPA + Hibernate ORM |
| Database | PostgreSQL 15 |
| Containerization | Docker + Docker Compose |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Key Features

###  Authentication & Security
- BCrypt password hashing with cost factor 10
- Stateless JWT authentication — no server-side sessions
- Role-based access control (CUSTOMER / ADMIN)
- `JwtFilter` intercepts every request, validates token, populates `SecurityContext`

###  Account Management
- Unique account number generation (ACC-XXXXXX format)
- `BigDecimal` balances with 4 decimal precision — no floating point errors
- `@Version` optimistic locking prevents concurrent balance corruption
- Account types: SAVINGS, CURRENT, LOAN

###  Transaction Engine
- **Double-entry ledger**: debit sender = credit receiver (atomic via `@Transactional`)
- Immutable transaction records — status updates only, never deletes
- Unique transaction reference (TXN-YYYYMMDD-XXXXXX)
- Full audit trail with `initiatedBy`, timestamp, and status tracking

###  Fraud Detection
Rule-based engine evaluating 3 signals:
1. **High value** — transactions above ₹50,000
2. **Velocity check** — more than 10 transactions per hour from same account
3. **Unusual hours** — transactions between 1 AM and 4 AM

###  Loan Processing
- EMI formula: `P × r × (1+r)^n / ((1+r)^n - 1)`
- `MathContext.DECIMAL128` for high-precision financial math
- Loan lifecycle: PENDING → APPROVED → ACTIVE → CLOSED

---

## API Endpoints

### Auth Service
```
POST /api/auth/register    — Register new user
POST /api/auth/login       — Login and receive JWT token
```

### Account Service
```
POST /api/accounts                      — Create new account
GET  /api/accounts/{accountNumber}      — Get account details
GET  /api/accounts/user/{email}         — Get all accounts for a user
POST /api/accounts/{accNum}/credit      — Credit balance
POST /api/accounts/{accNum}/debit       — Debit balance
```

### Transaction Service
```
POST /api/transactions/transfer             — Transfer between accounts
GET  /api/transactions/history/{accNum}     — Transaction history
GET  /api/transactions/{ref}                — Get by reference
```

### Loan Service
```
POST /api/loans/apply           — Apply for loan
PUT  /api/loans/{ref}/approve   — Approve loan (admin)
PUT  /api/loans/{ref}/repay     — Pay EMI instalment
```

### Notification Service
```
POST /api/notifications/transfer   — Send transfer alert
POST /api/notifications/login      — Send login alert
POST /api/notifications/fraud      — Send fraud alert
```

---

## Quick Start

### Option 1 — Docker (One command)
```bash
git clone https://github.com/Bhakti-909/fintech-system.git
cd fintech-system
docker-compose up --build
```
Then open `frontend/index.html` in your browser.

### Option 2 — Run Locally in IntelliJ
1. Install Java 17 and PostgreSQL
2. Create 5 databases: `fintech_auth`, `fintech_accounts`, `fintech_transactions`, `fintech_loans`, `fintech_notifications`
3. Update `spring.datasource.password` in each service's `application.properties`
4. Open each service in IntelliJ → run `XxxServiceApplication.java`
5. Start order: auth → account → transaction → loan → notification

---

## Sample Requests

**Register**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"bhakti@fintech.com","password":"Test@123","role":"CUSTOMER"}'
```

**Login**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"bhakti@fintech.com","password":"Test@123"}'
```

**Transfer Money**
```bash
curl -X POST http://localhost:8083/api/transactions/transfer \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"fromAccount":"ACC-111111","toAccount":"ACC-222222","amount":5000,"description":"Rent","initiatedBy":"bhakti@fintech.com"}'
```

---

## Project Structure

```
fintech-system/
├── auth-service/
│   └── src/main/java/com/fintech/auth/
│       ├── controller/       AuthController, GlobalExceptionHandler
│       ├── service/          AuthService
│       ├── security/         JwtUtil, JwtFilter, SecurityConfig
│       ├── entity/           User
│       ├── repository/       UserRepository
│       └── dto/              LoginRequest, RegisterRequest, AuthResponse
├── account-service/
├── transaction-service/
├── loan-service/
├── notification-service/
├── frontend/
│   ├── index.html            Banking dashboard UI
│   └── service-monitor.html  Health check monitor
├── docker-compose.yml
└── init-db.sql
```

---

## Design Decisions

**Why microservices over a monolith?**
Each service scales and deploys independently. The transaction service can handle high load without redeploying auth or notifications.

**Why database-per-service?**
No service reads another service's database directly. All cross-service communication goes through REST APIs — true loose coupling, a core microservices principle.

**Why stateless JWT over sessions?**
Any service instance can validate a JWT using the shared secret — no shared session store needed. Essential for horizontal scaling in a distributed system.

**Why BigDecimal for money?**
`double` cannot represent many decimal values exactly — `0.1 + 0.2 = 0.30000000000000004` in Java. Financial calculations require exact decimal arithmetic. Even fractions of a rupee matter.

---



**Bhakti Sainathi Kale**  
B.Tech Information Technology — MGM University, Chhatrapati Sambhajinagar  
[GitHub](https://github.com/Bhakti-909) · [Email](mailto:bskpatil909@gmail.com) · [LinkedIn](https://linkedin.com)
