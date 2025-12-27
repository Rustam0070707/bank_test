package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepo cardRepo;

    public CardService(CardRepo cardRepo) {
        this.cardRepo = cardRepo;
    }

    public Card createCard(Card card) {

        return cardRepo.save(card);
    }

    public Page<Card> getAllCards(String status, String q, Pageable pageable) {

        if (status == null && (q == null || q.isEmpty())) {
            return cardRepo.findAll(pageable);
        }

        if (status != null && (q == null || q.isEmpty())) {
            return cardRepo.findByStatus(CardStatus.valueOf(status), pageable);
        }

        if (status == null) {
            return cardRepo.findByOwnerUsernameContainingIgnoreCase(q, pageable);
        }

        return cardRepo.findByStatusAndOwnerUsernameContainingIgnoreCase(
                CardStatus.valueOf(status),
                q,
                pageable
        );
    }
}
