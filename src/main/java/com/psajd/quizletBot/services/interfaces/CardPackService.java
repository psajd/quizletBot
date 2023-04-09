package com.psajd.quizletBot.services.interfaces;

import com.psajd.quizletBot.entities.CardPack;

import java.util.List;

public interface CardPackService {
    CardPack addCardPack(CardPack cardPack);


    void deleteCardPack(CardPack cardPack);

    public List<CardPack> getPacksByPersonId(Long id);

    public CardPack getCardPackByName(String name);
}
