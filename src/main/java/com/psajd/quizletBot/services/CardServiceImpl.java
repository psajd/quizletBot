package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.repositories.CardRepository;
import com.psajd.quizletBot.services.interfaces.CardService;
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
    public void deleteCard(Card card) {
        cardRepository.deleteById(card.getId());
    }

    @Override
    public Card findCardByTermAndPersonAndPackName(Long id, String term, String packName) {
        return cardRepository.findByCardPack_Person_IdAndTermAndCardPack_Name(id, term, packName);
    }

    @Autowired
    public void setCardRepository(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
}
