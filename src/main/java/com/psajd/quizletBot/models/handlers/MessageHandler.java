package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.caching.BotStateCash;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageHandler {

    private ServiceAggregator serviceAggregator;
    private BotStateCash botStateCash;

    private MainMenuEventsHandler mainMenuEventsHandler;


    public BotApiMethod<?> handle(Message message, BotState botState) {
        long chatId = message.getChatId();

        if (serviceAggregator.getPersonService().getPersonById(chatId) == null) {
            return mainMenuEventsHandler.saveNewPerson(message);
        }

        botStateCash.saveBotState(chatId, botState);

        switch (botState) {
            case ON_START -> {
                return mainMenuEventsHandler.getStartMessage(chatId, message);
            }
            case ON_ALL_PACKS -> {
                return mainMenuEventsHandler.getAllPacks(chatId);
            }
            case ON_PACK_CREATION_START -> {
                return mainMenuEventsHandler.addNewPack(chatId);
            }
            case ON_PACK_CREATION_NAME -> {
                return mainMenuEventsHandler.addPackName(chatId, message);
            }
        }
        return null;
    }

    @Autowired
    public void setBotStateCash(BotStateCash botStateCash) {
        this.botStateCash = botStateCash;
    }

    @Autowired
    public void setServiceAggregator(ServiceAggregator serviceAggregator) {
        this.serviceAggregator = serviceAggregator;
    }

    @Autowired
    public void setMainMenuEventsHandler(MainMenuEventsHandler mainMenuEventsHandler) {
        this.mainMenuEventsHandler = mainMenuEventsHandler;
    }
}