package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;

import com.example.bankcards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        Card saved = cardService.createCard(card);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<Page<Card>> getAllCards(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                cardService.getAllCards(status, q, pageable)
        );
    }
}

