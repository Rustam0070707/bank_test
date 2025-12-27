package com.example.bankcards.Mapper;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;

public class CardMapper {
 public static CardResponse toResponse (Card card){
     return new CardResponse(card.getId(), card.getMaskedNumber(), card.getExpiryDate(), card.getStatus(), card.getBalance());

 }
}
