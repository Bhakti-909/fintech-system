package com.fintech.transaction.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String description;
    private String initiatedBy; // email from JWT token
}
