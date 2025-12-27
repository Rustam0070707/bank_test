package com.example.bankcards.util;

import org.springframework.stereotype.Component;

@Component
public class CardMaskUtil {

    public String mask(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}