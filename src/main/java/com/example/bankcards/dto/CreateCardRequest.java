package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCardRequest(String cardNumber,
                                LocalDate expiryDate,
                                BigDecimal balance,
                                Long userId) {
}
