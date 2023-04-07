package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.Card;

public interface CardService {
    Card addCard(Card card);

    Card updateCard(Card card);

    void deleteCard(Card card);

    Card findCardByTermAndPerson(Long term, String id);
}
