package com.psajd.quizletBot.models.handlers.extraHandlers;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.constants.BotMessages;
import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.models.bot.BotState;
import com.psajd.quizletBot.models.keyboards.InlineKeyboardFactory;
import com.psajd.quizletBot.models.keyboards.ReplyKeyboardFactory;
import com.psajd.quizletBot.models.caching.*;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class PackMenuHandler {

    private ServiceAggregator serviceAggregator;
    private CardPackCache cardPackCache;
    private BotStateCache botStateCache;
    private CardCache cardCache;
    private MainMenuEventsHandler mainMenuEventsHandler;
    private PracticeHandler practiceHandler;
    private ShowCardsHandler showCardsHandler;

    public BotApiMethod<?> choosePackInfoButton(Long chatId, Message message) {
        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            return mainMenuEventsHandler.getPacksTable(chatId);
        } else if (message.getText().equals(BotCommands.SHOW_CARDS.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_SHOW_CARDS);
            return showCardsHandler.showCards(chatId, message);
        } else if (message.getText().equals(BotCommands.PRACTICE.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_PRACTICE);
            return practiceHandler.startTraining(chatId);
        }
        return null;
    }

    public BotApiMethod<?> addNewCard(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.CREATING_CARD_ADD_QUESTION.getAnswer());
        botStateCache.saveBotState(chatId, BotState.ON_ADD_CARD_TERM);
        return sendMessage;
    }

    public BotApiMethod<?> removeCard(Long chatId) {
        botStateCache.saveBotState(chatId, BotState.ON_CARD_NAME_CHOICE);
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.DELETING_CARD_ENTER_CARD_TERM.getAnswer());
        sendMessage.setReplyMarkup(ReplyKeyboardFactory.createKeyboard(BotState.ON_PACK_CREATION_START));
        return sendMessage;
    }

    public BotApiMethod<?> choiceCardAndRemove(Long chatId, Message message) {
        Card card = serviceAggregator.getCardService().findCardByTermAndPersonAndPackName(
                chatId,
                message.getText(),
                cardPackCache.getCardPackMap().get(chatId).getName());

        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        if (card == null) {
            return new SendMessage(chatId.toString(), BotMessages.EXCEPTION_CARD_WAS_NOT_FOUND.getAnswer());
        }

        serviceAggregator.getCardService().deleteCard(card);
        mainMenuEventsHandler.executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_REMOVE.getAnswer()));
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
    }

    public BotApiMethod<?> addCardTerm(Long chatId, Message message) {
        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            cardCache.saveCard(chatId, null);
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        Card card = new Card();
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);
        if (cardPack.getCards().stream().anyMatch(x -> x.getTerm().equals(message.getText()))) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            mainMenuEventsHandler.executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.EXCEPTION_NOT_UNIQUE_CARD_TERM.getAnswer()));
            return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        card.setTerm(message.getText());
        botStateCache.saveBotState(chatId, BotState.ON_ADD_CARD_DEFINITION);
        cardCache.saveCard(chatId, card);
        return new SendMessage(chatId.toString(), BotMessages.CREATING_CARD_ADD_ANSWER.getAnswer());
    }

    public BotApiMethod<?> addCardDefinition(Long chatId, Message message) {
        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            cardCache.saveCard(chatId, null);
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        Card card = cardCache.getCardMap().get(chatId);
        if (card == null) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            return new SendMessage(chatId.toString(), BotMessages.ERROR_SOMETHING_WENT_WRONG.getAnswer());
        }

        card.setDefinition(message.getText());
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);
        cardPack.addCard(card);
        card.setNumber(cardPack.getCards().size());
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        serviceAggregator.getCardService().addCard(card);

        mainMenuEventsHandler.executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_ADD.getAnswer()));

        return mainMenuEventsHandler.getPackInfo(chatId, cardPack.getName());
    }

    public BotApiMethod<?> removeCardPackQuestion(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.QUESTION_ARE_YOU_SURE.getAnswer());
        sendMessage.setReplyMarkup(InlineKeyboardFactory.createKeyboard(BotState.ON_REMOVE_CARD_PACK));
        return sendMessage;
    }

    public BotApiMethod<?> removeCardPack(Long chatId) {
        serviceAggregator.getCardPackService().deleteCardPack(cardPackCache.getCardPackMap().get(chatId));
        mainMenuEventsHandler.executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_PACK_REMOVE.getAnswer()));
        botStateCache.saveBotState(chatId, BotState.ON_START);
        return mainMenuEventsHandler.getStartMessage(chatId);
    }

    @Autowired
    public void setServiceAggregator(ServiceAggregator serviceAggregator) {
        this.serviceAggregator = serviceAggregator;
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
    public void setPracticeHandler(PracticeHandler practiceHandler) {
        this.practiceHandler = practiceHandler;
    }

    @Autowired
    public void setShowCardsHandler(ShowCardsHandler showCardsHandler) {
        this.showCardsHandler = showCardsHandler;
    }
}
