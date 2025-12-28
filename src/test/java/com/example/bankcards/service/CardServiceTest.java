package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.repository.UserRepo;
import com.example.bankcards.util.CardCryptoUtil;
import com.example.bankcards.util.CardMaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepo cardRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CardCryptoUtil cardCryptoUtil;

    @Mock
    private CardMaskUtil cardMaskUtil;

    @InjectMocks
    private CardService cardService;

    private User testUser;
    private Card testCard;
    private CreateCardRequest createCardRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setOwner(testUser);
        testCard.setMaskedNumber("**** **** **** 1234");
        testCard.setEncryptedNumber("encrypted1234");
        testCard.setExpiryDate(LocalDate.now().plusYears(3));
        testCard.setBalance(BigDecimal.valueOf(1000));
        testCard.setStatus(CardStatus.ACTIVE);

        createCardRequest = new CreateCardRequest(
                "1234567890123456",
                LocalDate.now().plusYears(3),
                BigDecimal.valueOf(1000),
                1L
        );
    }

    @Test
    void createCard_WithValidRequest_ShouldReturnCardResponse() {

        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(cardMaskUtil.mask(anyString())).thenReturn("**** **** **** 3456");
        when(cardCryptoUtil.encrypt(anyString())).thenReturn("encrypted1234");
        when(cardRepo.save(any(Card.class))).thenReturn(testCard);


        CardResponse response = cardService.createCard(createCardRequest);


        assertNotNull(response);
        assertEquals(testCard.getId(), response.id());
        verify(cardRepo, times(1)).save(any(Card.class));
        verify(cardMaskUtil, times(1)).mask(createCardRequest.cardNumber());
        verify(cardCryptoUtil, times(1)).encrypt(createCardRequest.cardNumber());
    }

    @Test
    void createCard_WithNonExistentUser_ShouldThrowUserNotFoundException() {

        Long nonExistentUserId = 999L;
        CreateCardRequest request = new CreateCardRequest(
                "1234567890123456",
                LocalDate.now().plusYears(3),
                BigDecimal.valueOf(1000),
                nonExistentUserId
        );
        
        when(userRepo.findById(nonExistentUserId)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> 
            cardService.createCard(request),
            "Should throw UserNotFoundException when user is not found"
        );
        
        verify(userRepo).findById(nonExistentUserId);
        verifyNoMoreInteractions(cardRepo, cardMaskUtil, cardCryptoUtil);
    }

    @Test
    void blockCard_WithActiveCard_ShouldBlockCard() {

        when(cardRepo.findById(1L)).thenReturn(Optional.of(testCard));


        cardService.blockCard(1L);


        assertEquals(CardStatus.BLOCKED, testCard.getStatus());
        verify(cardRepo, times(1)).save(testCard);
    }

    @Test
    void blockCard_WithAlreadyBlockedCard_ShouldThrowInvalidCardStatusException() {

        testCard.setStatus(CardStatus.BLOCKED);
        when(cardRepo.findById(1L)).thenReturn(Optional.of(testCard));


        assertThrows(InvalidCardStatusException.class, () -> 
            cardService.blockCard(1L)
        );
    }

    @Test
    void blockCard_WithNonExistentCard_ShouldThrowCardNotFoundException() {

        when(cardRepo.findById(1L)).thenReturn(Optional.empty());


        assertThrows(CardNotFoundException.class, () -> 
            cardService.blockCard(1L)
        );
    }

    @Test
    void activateCard_WithBlockedCard_ShouldActivateCard() {

        testCard.setStatus(CardStatus.BLOCKED);
        when(cardRepo.findById(1L)).thenReturn(Optional.of(testCard));


        cardService.activateCard(1L);


        assertEquals(CardStatus.ACTIVE, testCard.getStatus());
        verify(cardRepo, times(1)).save(testCard);
    }

    @Test
    void getAllCards_WithNoFilters_ShouldReturnAllCards() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(List.of(testCard));
        when(cardRepo.findAll(pageable)).thenReturn(page);


        var result = cardService.getAllCards(null, null, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepo, times(1)).findAll(pageable);
    }

    @Test
    void getAllCards_WithStatusFilter_ShouldReturnFilteredCards() {

        Pageable pageable = PageRequest.of(0, 10);
        CardResponse cardResponse = new CardResponse(
                testCard.getId(),
                testCard.getMaskedNumber(),
                testCard.getExpiryDate(),
                testCard.getStatus(),
                testCard.getBalance()
        );
        Page<CardResponse> page = new PageImpl<>(List.of(cardResponse));
        when(cardRepo.findByStatus(CardStatus.ACTIVE, pageable)).thenReturn(page);


        var result = cardService.getAllCards("ACTIVE", null, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepo, times(1)).findByStatus(CardStatus.ACTIVE, pageable);
    }
}
