package com.psajd.quizletBot;

import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.services.CardPackServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class QuizletBotApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(QuizletBotApplication.class, args);
        CardPackServiceImpl cardPackServiceImpl = applicationContext.getBean("cardPackServiceImpl", CardPackServiceImpl.class);

/*

        Card card = new Card();
        card.setAnswer("new Answer(1L,");
        card.setQuestion("asdfasdf");
        CardPack cardPack = new CardPack();
        cardPack.addCard(card);
        cardPack.setName("popa");

        cardPackServiceImpl.addCardPack(cardPack);
*/

    }

}
