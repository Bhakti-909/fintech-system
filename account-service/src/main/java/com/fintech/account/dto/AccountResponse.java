package com.fintech.account.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String ownerEmail;
    private BigDecimal balance;
    private String accountType;
    private String status;
}
