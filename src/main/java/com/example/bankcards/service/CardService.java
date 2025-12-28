package com.example.bankcards.service;

import com.example.bankcards.Mapper.CardMapper;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.repository.UserRepo;
import com.example.bankcards.util.CardCryptoUtil;
import com.example.bankcards.util.CardMaskUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CardService {

    private final CardRepo cardRepo;
    private final UserRepo userRepo;
    private final CardCryptoUtil cardCryptoUtil;
    private final CardMaskUtil cardMaskUtil;
    public CardService(CardRepo cardRepo, UserRepo userRepo, CardCryptoUtil cardCryptoUtil, CardMaskUtil cardMaskUtil) {
        this.cardRepo = cardRepo;
        this.userRepo = userRepo;
        this.cardCryptoUtil = cardCryptoUtil;
        this.cardMaskUtil = cardMaskUtil;
    }

    public CardResponse createCard(CreateCardRequest request) {
        User owner = userRepo.findById(request.userId()).orElseThrow(() -> new UserNotFoundException("User not found"));
 Card card = new Card();
 card.setOwner(owner);
 card.setMaskedNumber(cardMaskUtil.mask(request.cardNumber()));
card.setEncryptedNumber(cardCryptoUtil.encrypt(request.cardNumber()));
 card.setExpiryDate(request.expiryDate());
 card.setBalance(request.balance());
 card.setStatus(CardStatus.ACTIVE);

       Card saved =  cardRepo.save(card);
        return CardMapper.toResponse(saved) ;
    }

    public Page<CardResponse> getAllCards(String status, String q, Pageable pageable) {

        if (status == null && (q == null || q.isEmpty())) {
            return cardRepo.findAll(pageable).map(CardMapper::toResponse);
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

    public void blockCard(Long id) {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new InvalidCardStatusException("Card is already blocked");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    public void activateCard(Long id) {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new InvalidCardStatusException("Card is already activated");
        }

        card.setStatus(CardStatus.ACTIVE);
        cardRepo.save(card);
    }

    public void deleteCard(Long id) {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        cardRepo.delete(card);
    }

    public void requestBlock(Long id, String name)  {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getOwner().getUsername().equals(name)) {
            throw new AccessDeniedException("This card is not yours");
        }
        card.setStatus(CardStatus.BLOCK_REQUESTED);
        cardRepo.save(card);
    }

    public BigDecimal getBalance(Long id, String name) {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getOwner().getUsername().equals(name)) {
            throw new AccessDeniedException("This card is not yours");
        }
        return card.getBalance();
    }


    public void transfer(@NotNull Long fromCardId,
                         @NotNull Long toCardId,
                         @NotNull @Positive BigDecimal amount,
                         String username) {
        if (fromCardId.equals(toCardId)) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }
        Card from = findAndValidateOwnership(fromCardId, username);
        Card to = findAndValidateOwnership(toCardId, username);


        validateCardIsActive(from);
        validateCardIsActive(to);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        cardRepo.save(from);
        cardRepo.save(to);
    }
    private void validateCardIsActive(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStatusException("Card is not active");
        }
    }
    private Card findAndValidateOwnership(Long cardId, String username) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("This card is not yours");
        }
        return card;
    }
}
