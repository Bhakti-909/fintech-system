package com.fintech.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// INTERVIEW: "The notification service is event-driven.
// Other services call it when something important happens (transfer, login, loan approved).
// In a production system, this would consume events from a Kafka topic.
// For now it logs notifications — easily replaceable with email (JavaMail) or SMS (Twilio)."
@Service
@Slf4j
public class NotificationService {

    public void sendTransferNotification(String senderEmail, String receiverEmail,
                                          BigDecimal amount, String txnRef) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        // In production: replace log statements with JavaMailSender or HTTP to email provider
        log.info("[NOTIFICATION] Debit alert sent to: {} | Amount: {} | Ref: {} | Time: {}",
                senderEmail, amount, txnRef, time);
        log.info("[NOTIFICATION] Credit alert sent to: {} | Amount: {} | Ref: {} | Time: {}",
                receiverEmail, amount, txnRef, time);
    }

    public void sendLoginAlert(String email, String ipAddress) {
        log.info("[NOTIFICATION] Login alert sent to: {} | IP: {} | Time: {}",
                email, ipAddress, LocalDateTime.now());
    }

    public void sendLoanApprovalNotification(String email, String loanRef,
                                              BigDecimal amount, BigDecimal emi) {
        log.info("[NOTIFICATION] Loan approval sent to: {} | Loan: {} | Amount: {} | EMI: {}",
                email, loanRef, amount, emi);
    }

    public void sendFraudAlert(String adminEmail, String suspiciousAccount,
                                BigDecimal amount, String txnRef) {
        log.warn("[FRAUD ALERT] Alert to admin: {} | Suspicious account: {} | Amount: {} | Ref: {}",
                adminEmail, suspiciousAccount, amount, txnRef);
    }
}
