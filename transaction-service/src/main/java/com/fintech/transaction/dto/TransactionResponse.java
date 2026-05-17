package com.fintech.transaction.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private String transactionRef;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private boolean fraudFlag;
    private LocalDateTime createdAt;
}
