package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCardRequest(String cardNumber,
                                LocalDate expirationDate,
                                BigDecimal initialBalance,
                                Long userId) {
}
