# Fintech System — Banking & Financial Management Platform

> Java · Spring Boot · PostgreSQL · Docker · Microservices · Spring Security · JWT

---

## Project Structure

```
fintech-system/
├── auth-service/          → Port 8081 — Register, Login, JWT
├── account-service/       → Port 8082 — Create accounts, balances
├── transaction-service/   → Port 8083 — Transfers, fraud detection
├── loan-service/          → Port 8084 — Loans, EMI calculation
├── notification-service/  → Port 8085 — Alerts and notifications
├── frontend/              → index.html — Dashboard UI
├── docker-compose.yml     → Runs everything with one command
└── init-db.sql            → Creates all 5 PostgreSQL databases
```

---

## Option 1 — Run with Docker (Easiest, one command)

### Prerequisites
- Install Docker Desktop: https://www.docker.com/products/docker-desktop
- Make sure Docker Desktop is running (whale icon in taskbar)

### Steps

```bash
# 1. Open terminal in the fintech-system folder
cd fintech-system

# 2. Run everything
docker-compose up --build

# 3. Wait ~2 minutes for all services to start
# You will see logs from all 5 services

# 4. Open frontend
# Open frontend/index.html in your browser (double-click the file)
```

### Stop everything
```bash
docker-compose down
```

---

## Option 2 — Run in IntelliJ IDEA (Step by step)

### Step 1 — Install Prerequisites

1. **Java 17** — Download from https://adoptium.net (select Java 17 LTS)
   - After install, verify: open terminal → type `java -version` → should show 17

2. **PostgreSQL** — Download from https://www.postgresql.org/download/
   - During install: set password as `postgres123`, port `5432`
   - After install, open **pgAdmin** (comes with PostgreSQL)

3. **IntelliJ IDEA** — Community edition is free: https://www.jetbrains.com/idea/download

---

### Step 2 — Create Databases in pgAdmin

Open pgAdmin → right-click "Databases" → "Create" → "Database"

Create these 5 databases one by one:
```
fintech_auth
fintech_accounts
fintech_transactions
fintech_loans
fintech_notifications
```

OR run this SQL in pgAdmin Query Tool:
```sql
CREATE DATABASE fintech_auth;
CREATE DATABASE fintech_accounts;
CREATE DATABASE fintech_transactions;
CREATE DATABASE fintech_loans;
CREATE DATABASE fintech_notifications;
```

---

### Step 3 — Open Project in IntelliJ

1. Open IntelliJ IDEA
2. Click **"Open"**
3. Navigate to `fintech-system/auth-service` folder → click **OK**
4. IntelliJ will detect it as a Maven project → click **"Load Maven Project"** when prompted
5. Wait for IntelliJ to download all dependencies (watch bottom progress bar)
6. **Repeat for each service** — open each one as a separate IntelliJ project OR use "File → Open" and open them all

**TIP:** Use IntelliJ Ultimate (free for students) — it lets you open all modules in one window.
Apply here: https://www.jetbrains.com/community/education/

---

### Step 4 — Configure application.properties

For each service, open `src/main/resources/application.properties` and update the password:

```properties
spring.datasource.password=postgres123
```

(Replace `your_password` with whatever you set during PostgreSQL install)

---

### Step 5 — Run Services in IntelliJ

**For each service:**

1. Open the service folder in IntelliJ
2. Find the main class:
   - auth-service → `AuthServiceApplication.java`
   - account-service → `AccountServiceApplication.java`
   - transaction-service → `TransactionServiceApplication.java`
   - loan-service → `LoanServiceApplication.java`
   - notification-service → `NotificationServiceApplication.java`
3. Right-click the file → **"Run 'XxxServiceApplication'"**
4. Watch the Console tab — wait for: `Started XxxServiceApplication in X.XXX seconds`

**Run order (important):**
```
1. auth-service        (8081) ← start first
2. account-service     (8082)
3. transaction-service (8083)
4. loan-service        (8084)
5. notification-service(8085) ← start last
```

---

### Step 6 — Open the Frontend Dashboard

1. Navigate to `fintech-system/frontend/`
2. Double-click `index.html`
3. It opens in your browser automatically

OR right-click `index.html` in IntelliJ → "Open In" → "Browser" → choose Chrome

---

### Step 7 — Test the Application

#### Register a user
```
1. On the dashboard → click "Register"
2. Enter email: bhakti@fintech.com
3. Enter password: Test@123
4. Select role: CUSTOMER
5. Click "Create account"
```

#### Create a bank account
```
1. Go to "Accounts" tab
2. Click "+ New Account"
3. Select type: SAVINGS
4. Click "Create Account"
5. Note the account number (e.g. ACC-123456)
```

#### Transfer money (you need 2 accounts)
```
1. Create a second account with a different email
2. Go to "Transfer" tab
3. Select From Account
4. Enter the second account number in "To Account"
5. Enter amount and click "Transfer Now"
```

---

## API Testing with Postman / curl

### Register
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"bhakti@fintech.com","password":"Test@123","role":"CUSTOMER"}'
```

### Login (get JWT token)
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"bhakti@fintech.com","password":"Test@123"}'
```

### Create Account (use token from login)
```bash
curl -X POST http://localhost:8082/api/accounts \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"ownerEmail":"bhakti@fintech.com","accountType":"SAVINGS"}'
```

### Transfer Money
```bash
curl -X POST http://localhost:8083/api/transactions/transfer \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"fromAccount":"ACC-111111","toAccount":"ACC-222222","amount":5000,"description":"Test transfer","initiatedBy":"bhakti@fintech.com"}'
```

### Apply for Loan
```bash
curl -X POST http://localhost:8084/api/loans/apply \
  -H "Content-Type: application/json" \
  -d '{"email":"bhakti@fintech.com","accountNumber":"ACC-111111","principal":"100000","annualRate":"12","tenureMonths":"24"}'
```

---

## Common Errors & Fixes

| Error | Fix |
|-------|-----|
| `Connection refused` on port 5432 | PostgreSQL is not running. Open pgAdmin or start PostgreSQL service |
| `Unknown database fintech_auth` | Run the CREATE DATABASE commands in Step 2 |
| `Port 8081 already in use` | Another service is using that port. Change `server.port` in application.properties |
| `Could not autowire` bean error | Maven dependencies not downloaded. Right-click pom.xml → "Maven" → "Reload project" |
| Frontend shows "Failed to fetch" | Backend services are not running. Start all 5 services first |
| Lombok not working | IntelliJ: Settings → Plugins → search "Lombok" → Install → Restart IntelliJ |

---

## How to Push to GitHub

```bash
# 1. Open terminal in fintech-system folder
cd fintech-system

# 2. Initialize git
git init

# 3. Add all files
git add .

# 4. First commit
git commit -m "Initial commit - Fintech microservices banking system"

# 5. Create repo on GitHub (github.com → New repository → name: fintech-system)

# 6. Connect and push
git remote add origin https://github.com/YOUR_USERNAME/fintech-system.git
git branch -M main
git push -u origin main
```

---

## Service Ports Summary

| Service | Port | Database |
|---------|------|----------|
| auth-service | 8081 | fintech_auth |
| account-service | 8082 | fintech_accounts |
| transaction-service | 8083 | fintech_transactions |
| loan-service | 8084 | fintech_loans |
| notification-service | 8085 | fintech_notifications |
| Frontend Dashboard | file:// | — |
