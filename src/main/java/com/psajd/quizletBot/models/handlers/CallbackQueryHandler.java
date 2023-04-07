package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.models.caching.BotStateCache;
import com.psajd.quizletBot.models.caching.CardCache;
import com.psajd.quizletBot.models.caching.CardPackCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CallbackQueryHandler {

    private CardPackCache cardPackCache;
    private BotStateCache botStateCache;
    private CardCache cardCache;
    private MainMenuEventsHandler mainMenuEventsHandler;

    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        Message message = callbackQuery.getMessage();
        BotApiMethod<?> callBackAnswer = null;

        if (data.equals(BotCommands.ADD_CARD.getCommand())) {
            callBackAnswer = mainMenuEventsHandler.addNewCard(chatId, callbackQuery.getMessage());
        } else if (data.equals(BotCommands.REMOVE_CARD.getCommand())) {
            callBackAnswer = mainMenuEventsHandler.removeCard(chatId, message);
        } else if (data.equals(BotCommands.REMOVE_CARD_PACK.getCommand())) {
            callBackAnswer = mainMenuEventsHandler.removeCardPack(chatId, message);
        } else if (data.equals(BotCommands.CHANGE_NAME.getCommand())) {

        } else if (data.equals(BotCommands.SHOW_PACK_STATISTIC.getCommand())) {

        }

        return callBackAnswer;
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
    public void setMainMenuEventsHandler(MainMenuEventsHandler mainMenuEventsHandler) {
        this.mainMenuEventsHandler = mainMenuEventsHandler;
    }

    @Autowired
    public void setCardCache(CardCache cardCache) {
        this.cardCache = cardCache;
    }
}
