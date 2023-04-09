package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.caching.BotStateCache;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageHandler {

    private ServiceAggregator serviceAggregator;
    private BotStateCache botStateCache;

    private MainMenuEventsHandler mainMenuEventsHandler;


    public BotApiMethod<?> handle(Message message, BotState botState) {
        long chatId = message.getChatId();

        if (serviceAggregator.getPersonService().getPersonById(chatId) == null) {
            return mainMenuEventsHandler.saveNewPerson(message);
        }

        botStateCache.saveBotState(chatId, botState);

        switch (botState) {
            case ON_START -> {
                return mainMenuEventsHandler.getStartMessage(chatId);
            }
            case ON_PACK_CREATION_START -> {
                return mainMenuEventsHandler.addNewPack(chatId);
            }
            case ON_PACK_CREATION_NAME -> {
                return mainMenuEventsHandler.addPackName(chatId, message);
            }
            case ON_PACK_TABLE -> {
                return mainMenuEventsHandler.getPacksTable(chatId);
            }
            case ON_CHOOSE_PACK -> {
                return mainMenuEventsHandler.choosePack(chatId, message);
            }
            case ON_PACK_INFO -> {
                return mainMenuEventsHandler.choosePackInfoButton(chatId, message);
            }
            case ON_ADD_CARD_TERM -> {
                return mainMenuEventsHandler.addCardTerm(chatId, message);
            }
            case ON_ADD_CARD_DEFINITION -> {
                return mainMenuEventsHandler.addCardDefinition(chatId, message);
            }
            case ON_CARD_NAME_CHOICE -> {
                return mainMenuEventsHandler.choiceCardAndRemove(chatId, message);
            }
            case ON_SHOW_CARDS -> {
                return mainMenuEventsHandler.showCards(chatId, message);
            }
            case ON_CHECK_CORRECT_ANSWER -> {
                return mainMenuEventsHandler.checkCorrectAnswer(chatId, message);
            }
        }
        return null;
    }

    @Autowired
    public void setBotStateCash(BotStateCache botStateCache) {
        this.botStateCache = botStateCache;
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
