package com.psajd.quizletBot.models.handlers.mainHandlers;

import com.psajd.quizletBot.models.bot.BotState;
import com.psajd.quizletBot.models.caching.BotStateCache;
import com.psajd.quizletBot.models.handlers.extraHandlers.MainMenuEventsHandler;
import com.psajd.quizletBot.models.handlers.extraHandlers.PackMenuHandler;
import com.psajd.quizletBot.models.handlers.extraHandlers.PracticeHandler;
import com.psajd.quizletBot.models.handlers.extraHandlers.ShowCardsHandler;
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
    private PackMenuHandler packMenuHandler;
    private PracticeHandler practiceHandler;
    private ShowCardsHandler showCardsHandler;

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
                return packMenuHandler.choosePackInfoButton(chatId, message);
            }
            case ON_ADD_CARD_TERM -> {
                return packMenuHandler.addCardTerm(chatId, message);
            }
            case ON_ADD_CARD_DEFINITION -> {
                return packMenuHandler.addCardDefinition(chatId, message);
            }
            case ON_CARD_NAME_CHOICE -> {
                return packMenuHandler.choiceCardAndRemove(chatId, message);
            }
            case ON_SHOW_CARDS -> {
                return showCardsHandler.showCards(chatId, message);
            }
            case ON_CHECK_CORRECT_ANSWER -> {
                return practiceHandler.checkCorrectAnswer(chatId, message);
            }
            case ON_PACK_UPDATE_NAME -> {
                return packMenuHandler.renameCardPack(chatId, message);
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

    @Autowired
    public void setBotStateCache(BotStateCache botStateCache) {
        this.botStateCache = botStateCache;
    }

    @Autowired
    public void setPackMenuHandler(PackMenuHandler packMenuHandler) {
        this.packMenuHandler = packMenuHandler;
    }

    @Autowired
    public void setPracticeHandler(PracticeHandler practiceHandler) {
        this.practiceHandler = practiceHandler;
    }

    @Autowired
    public void setShowCardsHandler(ShowCardsHandler showCardsHandler) {
        this.showCardsHandler = showCardsHandler;
    }
}
