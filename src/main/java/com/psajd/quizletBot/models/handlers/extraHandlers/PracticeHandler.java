package com.psajd.quizletBot.models.handlers.extraHandlers;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.constants.BotMessages;
import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.models.bot.BotState;
import com.psajd.quizletBot.models.keyboards.ReplyKeyboardFactory;
import com.psajd.quizletBot.models.caching.*;
import com.psajd.quizletBot.services.interfaces.CardPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PracticeHandler {
    private CardPackCache cardPackCache;
    private BotStateCache botStateCache;
    private CardCache cardCache;
    private MainMenuEventsHandler mainMenuEventsHandler;

    private CardPackService cardPackService;


    public BotApiMethod<?> startTraining(Long chatId) {
        List<Card> cards = cardPackCache
                .getCardPackMap()
                .get(chatId)
                .getCards()
                .stream()
                .toList();
        if (cards.size() < 4) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            mainMenuEventsHandler.executeAdditionalMethod(new SendMessage(chatId.toString(),
                    "Minimal amount of cards for practice is 4, add some."));
            return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }

        List<Card> variants = pickNRandomCards(cards, 4);
        Card correctCard = variants.get(0);
        Collections.shuffle(variants);
        cardCache.saveCard(chatId, correctCard);

        SendMessage sendMessage = new SendMessage(chatId.toString(), "Choose correct term:\n\n" +
                "... - " + correctCard.getDefinition());
        sendMessage.setReplyMarkup(ReplyKeyboardFactory.onPractice(variants));
        botStateCache.saveBotState(chatId, BotState.ON_CHECK_CORRECT_ANSWER);
        return sendMessage;
    }

    public BotApiMethod<?> checkCorrectAnswer(Long chatId, Message message) {
        if (message.getText().equals(
                BotCommands
                        .GO_BACK
                        .getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        } else if (message.getText().equals(
                cardCache
                        .getCardMap()
                        .get(chatId)
                        .getTerm())) {
            mainMenuEventsHandler.executeAdditionalMethod(new SendMessage(chatId.toString(),
                    BotMessages.SUCCESSFUL_CORRECT_ANSWER.getAnswer()));
            addCorrectAnswer(cardPackCache.getCardPackMap().get(chatId));
            return startTraining(chatId);
        } else {
            mainMenuEventsHandler.executeAdditionalMethod(new SendMessage(chatId.toString(),
                    BotMessages.SUCCESSFUL_WRONG_ANSWER.getAnswer() + cardCache.getCardMap().get(chatId).getTerm()));
            addWrongAnswer(cardPackCache.getCardPackMap().get(chatId));
            return startTraining(chatId);
        }
    }

    private void addCorrectAnswer(CardPack cardPack) {
        if (cardPack.getCorrectAnswers() == null) {
            cardPack.setCorrectAnswers(1L);
        } else {
            cardPack.setCorrectAnswers(1L + cardPack.getCorrectAnswers());
        }
        cardPackService.updateCardPack(cardPack);
    }

    private void addWrongAnswer(CardPack cardPack) {
        if (cardPack.getWrongAnswers() == null) {
            cardPack.setWrongAnswers(1L);
        } else {
            cardPack.setWrongAnswers(1L + cardPack.getWrongAnswers());
        }
        cardPackService.updateCardPack(cardPack);
    }

    private List<Card> pickNRandomCards(List<Card> lst, int n) {
        List<Card> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
    }

    @Autowired
    public void setCardPackCache(CardPackCache cardPackCache) {
        this.cardPackCache = cardPackCache;
    }

    @Autowired
    public void setBotStateCache(BotStateCache botStateCache) {
        this.botStateCache = botStateCache;
    }

    @Autowired
    public void setCardCache(CardCache cardCache) {
        this.cardCache = cardCache;
    }

    @Autowired
    public void setMainMenuEventsHandler(MainMenuEventsHandler mainMenuEventsHandler) {
        this.mainMenuEventsHandler = mainMenuEventsHandler;
    }

    @Autowired
    public void setCardPackService(CardPackService cardPackService) {
        this.cardPackService = cardPackService;
    }
}
