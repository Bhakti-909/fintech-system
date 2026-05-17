package com.fintech.account.dto;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String ownerEmail;
    private String accountType;  // SAVINGS, CURRENT, LOAN
}
