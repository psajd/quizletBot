package com.psajd.quizletBot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceAggregator {
    private final CardPackService cardPackService;

    private final PersonService personService;

    @Autowired
    public ServiceAggregator(CardPackService cardPackService, PersonService personService) {
        this.cardPackService = cardPackService;
        this.personService = personService;
    }

    public CardPackService getCardPackService() {
        return cardPackService;
    }

    public PersonService getPersonService() {
        return personService;
    }
}
