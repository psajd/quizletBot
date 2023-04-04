package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.repositories.CardPackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardPackServiceImpl implements CardPackService {

    private final CardPackRepository cardPackRepository;


    @Autowired
    public CardPackServiceImpl(CardPackRepository cardPackRepository) {
        this.cardPackRepository = cardPackRepository;
    }

    @Override
    public CardPack addCardPack(CardPack cardPack) {
        return cardPackRepository.save(cardPack);
    }

    @Override
    public CardPack updateCardPack(CardPack cardPack) {
        return null;
    }

    @Override
    public void deleteCardPack(CardPack cardPack) {

    }

    @Override
    public CardPack getCardPackById(Long id) {
        return null;
    }

    public List<CardPack> getPacksByPersonId(Long id) {
        return cardPackRepository.findByPerson_Id(id);
    }
}
