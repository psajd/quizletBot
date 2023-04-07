package com.psajd.quizletBot.repositories;

import com.psajd.quizletBot.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByCardPack_Person_IdAndTerm(Long id, String term);
    @Override
    void deleteById(Long aLong);
}
