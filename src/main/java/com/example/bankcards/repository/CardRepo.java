package com.example.bankcards.repository;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepo extends JpaRepository<Card, Long > {

    Page<CardResponse> findByStatus(CardStatus status, Pageable pageable);

    Page<CardResponse> findByOwnerUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<CardResponse> findByStatusAndOwnerUsernameContainingIgnoreCase(
            CardStatus status,
            String username,
            Pageable pageable
    );



}
