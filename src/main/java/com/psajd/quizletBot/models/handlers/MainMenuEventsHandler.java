package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotAnswers;
import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.entities.Person;
import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.caching.BotStateCache;
import com.psajd.quizletBot.models.KeyboardFactory;
import com.psajd.quizletBot.models.caching.CardPackCache;
import com.psajd.quizletBot.models.caching.TableCache;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

@Component
public class MainMenuEventsHandler {

    private final int tableRows = 2;
    private final int tableColumns = 3;
    private ServiceAggregator serviceAggregator;
    private CardPackCache cardPackCache;
    private BotStateCache botStateCache;

    private TableCache tableCache;

    public SendMessage getStartMessage(long chatId, Message inputMessage) {
        SendMessage message = new SendMessage(String.valueOf(chatId), BotAnswers.SUCCESSFUL_START_MESSAGE.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = KeyboardFactory.createKeyboard(BotState.ON_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        return message;
    }

    public SendMessage getAllPacks(long chatId) {
        List<CardPack> cardPacks = serviceAggregator
                .getCardPackService()
                .getPacksByPersonId(chatId);
        StringBuilder stringBuilder = new StringBuilder();
        if (cardPacks.isEmpty()) {
            stringBuilder.append(BotAnswers.ERROR_ALL_PERSON_PACKS.getAnswer());
        } else {
            stringBuilder.append(BotAnswers.SUCCESSFUL_ALL_PERSON_PACKS.getAnswer()).append("\n\n");
            for (int i = 0; i < cardPacks.size(); i++) {
                CardPack cardPack = cardPacks.get(i);
                stringBuilder.append(i + 1).append(".").append("\n");
                stringBuilder.append("Card pack name: ").append(cardPack.getName()).append("\n");
                stringBuilder.append("Cards amount: ").append(cardPack.getCards().size()).append("\n");
                stringBuilder.append("\n");
            }
        }
        return new SendMessage(String.valueOf(chatId), stringBuilder.toString());
    }

    public SendMessage addNewPack(Long chatId) {
        CardPack cardPack = new CardPack();
        cardPackCache.saveCardPack(chatId, cardPack);
        SendMessage message = new SendMessage(String.valueOf(chatId), BotAnswers.CREATING_CARD_PACK_NAME.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = KeyboardFactory.createKeyboard(BotState.ON_PACK_CREATION_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        botStateCache.saveBotState(chatId, BotState.ON_PACK_CREATION_NAME);
        return message;
    }

    public SendMessage addPackName(Long chatId, Message message) {
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);

        if (message.getText().equals("Go back ⬇️")) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            return getStartMessage(chatId, message);
        }
        if (message.getText().isBlank()) {
            return new SendMessage(chatId.toString(), BotAnswers.WRONG_CARD_PACK_NAME.getAnswer());
        }

        Person person = serviceAggregator.getPersonService().getPersonById(chatId);
        person.addCardPack(cardPack);
        cardPack.setName(message.getText());

        serviceAggregator.getCardPackService().updateCardPack(cardPack);

        botStateCache.saveBotState(chatId, BotState.ON_START);
        SendMessage result = new SendMessage(chatId.toString(), BotAnswers.SUCCESSFUL_CARD_PACK_ADD.getAnswer());
        result.setReplyMarkup(KeyboardFactory.createKeyboard(BotState.ON_START));
        return result;
    }

    public BotApiMethod<?> saveNewPerson(Message message) {
        Person person = new Person();
        person.setName(message.getFrom().getUserName());
        person.setId(message.getChatId());
        serviceAggregator.getPersonService().addPerson(person);
        return getStartMessage(message.getChatId(), message);
    }

    public SendMessage getPacksTable(Long chatId) {
        List<CardPack> cardPacks = serviceAggregator.getCardPackService().getPacksByPersonId(chatId);
        int maxTablesAmount = cardPacks.size() / (tableColumns * tableRows) + 1;
        if (tableCache.getTableNumberMap().get(chatId) == null) {
            tableCache.saveNumber(chatId, 1);
        }
        int tableNumber = tableCache.getTableNumberMap().get(chatId);

        CardPack[][] rawTable = new CardPack[tableRows][tableColumns];
        for (int i = 0; i < tableRows; i++) {
            for (int j = 0; j < tableColumns; j++) {
                int index = i * tableColumns + j + (tableNumber - 1) * tableColumns * tableRows;
                rawTable[i][j] = index >= cardPacks.size() ? null : cardPacks.get(index);
            }
        }

        ReplyKeyboardMarkup keyboardMarkup = KeyboardFactory.createChooseTableKeyboard(rawTable, tableNumber, maxTablesAmount);

        SendMessage sendMessage = new SendMessage(chatId.toString(), BotAnswers.SUCCESSFUL_CARD_PACK_CHOOSE_TABLE.getAnswer());
        sendMessage.setReplyMarkup(keyboardMarkup);
        botStateCache.saveBotState(chatId, BotState.ON_CHOOSE_PACK);
        return sendMessage;
    }

    public SendMessage choosePack(Long chatId, Message message) {
        if (message.getText().equals("➡️")) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            tableCache.saveNumber(chatId, tableCache.getTableNumberMap().get(chatId) + 1);
            return getPacksTable(chatId);
        } else if (message.getText().equals("⬅️")) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            tableCache.saveNumber(chatId, tableCache.getTableNumberMap().get(chatId) - 1);
            return getPacksTable(chatId);
        } else if (message.getText().equals("Go back ⬇️")) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            tableCache.saveNumber(chatId, null);
            return getStartMessage(chatId, message);
        }
        tableCache.saveNumber(chatId, null);
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        return getPackInfo(chatId, message);
    }

    public SendMessage getPackInfo(Long chatId, Message message) {
        CardPack cardPack = serviceAggregator.getCardPackService().getCardPackByName(message.getText());
        if (cardPack == null) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            return getPacksTable(chatId);
        }

        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        SendMessage sendMessage = new SendMessage(chatId.toString(), "pack info: " + cardPack.getName());
        sendMessage.setReplyMarkup(KeyboardFactory.createKeyboard(BotState.ON_PACK_INFO));
        return sendMessage;
    }

    public SendMessage choosePackInfoButton(Long chatId, Message message) {
        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            return getPacksTable(chatId);
        } else if (message.getText().equals(BotCommands.SHOW_CARD.getCommand())) {
            return showCards(chatId, message);
        } else if (message.getText().equals(BotCommands.ADD_CARD.getCommand())) {
            return addNewCard(chatId, message);
        } else if (message.getText().equals(BotCommands.REMOVE_CARD.getCommand())) {
            return removeCard(chatId, message);
        } else if (message.getText().equals(BotCommands.TRAIN.getCommand())) {
            return startTraining(chatId, message);
        }
        return null;
    }

    public SendMessage startTraining(Long chatId, Message message) {

        return null;
    }

    public SendMessage showCards(Long chatId, Message message) {

        return null;

    }

    public SendMessage addNewCard(Long chatId, Message message) {
        return null;
    }

    public SendMessage removeCard(Long chatId, Message message) {
        return null;

    }

    public void addCardQuestion(Long chatId, Message message) {

    }

    public void addCardAnswer(Long chatId, Message message) {

    }

    @Autowired
    public void setServiceAggregator(ServiceAggregator serviceAggregator) {
        this.serviceAggregator = serviceAggregator;
    }

    @Autowired
    public void setBotStateCash(BotStateCache botStateCache) {
        this.botStateCache = botStateCache;
    }

    @Autowired
    public void setCardPackCash(CardPackCache cardPackCache) {
        this.cardPackCache = cardPackCache;
    }

    @Autowired
    public void setTableCache(TableCache tableCache) {
        this.tableCache = tableCache;
    }
}
