package com.fintech.auth.dto;

import lombok.Data;

// INTERVIEW: "DTOs (Data Transfer Objects) keep API contracts separate from
// database entities. We never expose entity objects directly in responses."

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String role; // "CUSTOMER" or "ADMIN"
}
