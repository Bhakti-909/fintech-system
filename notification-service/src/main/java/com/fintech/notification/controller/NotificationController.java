package com.fintech.notification.controller;

import com.fintech.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> notifyTransfer(@RequestBody Map<String, String> payload) {
        notificationService.sendTransferNotification(
            payload.get("senderEmail"),
            payload.get("receiverEmail"),
            new BigDecimal(payload.get("amount")),
            payload.get("txnRef")
        );
        return ResponseEntity.ok(Map.of("message", "Notification sent"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> notifyLogin(@RequestBody Map<String, String> payload) {
        notificationService.sendLoginAlert(payload.get("email"), payload.get("ip"));
        return ResponseEntity.ok(Map.of("message", "Login alert sent"));
    }

    @PostMapping("/fraud")
    public ResponseEntity<Map<String, String>> notifyFraud(@RequestBody Map<String, String> payload) {
        notificationService.sendFraudAlert(
            payload.get("adminEmail"),
            payload.get("suspiciousAccount"),
            new BigDecimal(payload.get("amount")),
            payload.get("txnRef")
        );
        return ResponseEntity.ok(Map.of("message", "Fraud alert sent"));
    }
}
