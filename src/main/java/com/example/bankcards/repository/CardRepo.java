package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
public interface CardRepo extends JpaRepository<Card, Integer > {

    Page<Card> findByStatus(CardStatus status, Pageable pageable);

    Page<Card> findByOwnerUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<Card> findByStatusAndOwnerUsernameContainingIgnoreCase(
            CardStatus status,
            String username,
            Pageable pageable
    );
}
