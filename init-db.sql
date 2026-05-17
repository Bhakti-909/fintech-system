-- INTERVIEW: "I used database-per-service pattern.
-- Each microservice owns its own database schema.
-- No service reads another service's DB directly — they communicate via REST APIs.
-- This is a core microservices principle: loose coupling."

CREATE DATABASE fintech_auth;
CREATE DATABASE fintech_accounts;
CREATE DATABASE fintech_transactions;
CREATE DATABASE fintech_loans;
CREATE DATABASE fintech_notifications;
