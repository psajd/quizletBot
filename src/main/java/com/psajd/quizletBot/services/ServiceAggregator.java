package com.psajd.quizletBot.services;

import com.psajd.quizletBot.services.interfaces.CardPackService;
import com.psajd.quizletBot.services.interfaces.CardService;
import com.psajd.quizletBot.services.interfaces.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceAggregator {
    private final CardPackService cardPackService;

    private final PersonService personService;

    private final CardService cardService;

    @Autowired
    public ServiceAggregator(CardPackService cardPackService, PersonService personService, CardService cardService) {
        this.cardPackService = cardPackService;
        this.personService = personService;
        this.cardService = cardService;
    }

    public CardPackService getCardPackService() {
        return cardPackService;
    }

    public PersonService getPersonService() {
        return personService;
    }


    public CardService getCardService() {
        return cardService;
    }
}
