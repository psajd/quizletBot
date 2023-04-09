package com.psajd.quizletBot.models.handlers.mainHandlers;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.models.bot.BotState;
import com.psajd.quizletBot.models.caching.BotStateCache;
import com.psajd.quizletBot.models.caching.CardPackCache;
import com.psajd.quizletBot.models.handlers.extraHandlers.MainMenuEventsHandler;
import com.psajd.quizletBot.models.handlers.extraHandlers.PackMenuHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CallbackQueryHandler {

    private CardPackCache cardPackCache;
    private BotStateCache botStateCache;
    private MainMenuEventsHandler mainMenuEventsHandler;
    private PackMenuHandler packMenuHandler;

    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        Message message = callbackQuery.getMessage();
        BotApiMethod<?> callBackAnswer = null;

        if (data.equals(BotCommands.ADD_CARD.getCommand())) {
            callBackAnswer = packMenuHandler.addNewCard(chatId);
        } else if (data.equals(BotCommands.REMOVE_CARD.getCommand())) {
            callBackAnswer = packMenuHandler.removeCard(chatId);
        } else if (data.equals(BotCommands.REMOVE_CARD_PACK.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_REMOVE_CARD_PACK);
            callBackAnswer = packMenuHandler.removeCardPackQuestion(chatId);
        } else if (data.equals(BotCommands.CARD_PACK_REMOVE_YES.getCommand())) {
            callBackAnswer = packMenuHandler.removeCardPack(chatId);
        } else if (data.equals(BotCommands.CARD_PACK_REMOVE_NO.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            callBackAnswer = mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        } else if (data.equals(BotCommands.CHANGE_NAME.getCommand())) {

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
    public void setPackMenuHandler(PackMenuHandler packMenuHandler) {
        this.packMenuHandler = packMenuHandler;
    }
}
