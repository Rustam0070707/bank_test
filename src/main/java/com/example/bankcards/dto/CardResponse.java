package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(Long id,
                           String maskedNumber,
                           LocalDate expiryDate,
                           CardStatus status,
                           BigDecimal balance) {
}
