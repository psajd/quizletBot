package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService {

    CardRepository cardRepository;

    @Override
    public Card addCard(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public Card updateCard(Card card) {
        return null;
    }

    @Override
    public void deleteCard(Card card) {
        cardRepository.deleteById(card.getId());
    }

    @Override
    public Card findCardByTermAndPerson(Long id, String term) {
        return cardRepository.findByCardPack_Person_IdAndTerm(id, term);
    }

    @Autowired
    public void setCardRepository(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
}