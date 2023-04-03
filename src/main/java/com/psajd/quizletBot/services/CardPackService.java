package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.CardPack;

public interface CardPackService {
    CardPack addCardPack(CardPack cardPack);

    CardPack updateCardPack(CardPack cardPack);

    void deleteCardPack(CardPack cardPack);

    CardPack getCardPackById(Long id);

}
