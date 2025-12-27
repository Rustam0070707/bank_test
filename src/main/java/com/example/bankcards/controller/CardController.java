package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;

import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/createCard")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<CardResponse> createCard(@RequestBody CreateCardRequest request) {
        CardResponse saved = cardService.createCard(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/getAllCards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                cardService.getAllCards(status, q, pageable)
        );
    }

    @PutMapping("/block/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockCard(@PathVariable Long id){
        cardService.blockCard(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateCard(@PathVariable Long id){
        cardService.activateCard(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Card> deleteCard(@PathVariable Long id){
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();

    }
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CardResponse>> myCards(
            Pageable pageable,
            Authentication authentication
    ){
        return ResponseEntity.ok(
                cardService.getAllCards(null, authentication.getName(), pageable)
        );

    }
    @GetMapping("/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> requestBlock(
            @PathVariable Long id ,
            Authentication authentication
    ){
        cardService.requestBlock(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable Long id ,
            Authentication authentication
    ){
        return ResponseEntity.ok(cardService.getBalance(id, authentication.getName()));
    }
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication
    ){
        cardService.transfer(request.fromCardId(), request.toCardId(), request.amount()  ,authentication.getName() );
        return ResponseEntity.ok().build();
    }



}

