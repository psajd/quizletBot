package com.psajd.quizletBot.models.handlers.extraHandlers;

import com.psajd.quizletBot.constants.BotMessages;
import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.entities.Person;
import com.psajd.quizletBot.models.bot.BotState;
import com.psajd.quizletBot.models.keyboards.InlineKeyboardFactory;
import com.psajd.quizletBot.models.bot.QuizletBot;
import com.psajd.quizletBot.models.caching.*;
import com.psajd.quizletBot.models.keyboards.ReplyKeyboardFactory;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class MainMenuEventsHandler {

    private final int tableRows = 2;
    private final int tableColumns = 3;
    private ApplicationContext applicationContext;
    private ServiceAggregator serviceAggregator;
    private CardPackCache cardPackCache;
    private BotStateCache botStateCache;
    private TableCache tableCache;

    protected void executeAdditionalMethod(BotApiMethod<?> method) {
        QuizletBot quizletBot = applicationContext.getBean("springWebhookBot", QuizletBot.class);
        try {
            quizletBot.execute(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public BotApiMethod<?> getStartMessage(long chatId) {
        SendMessage message = new SendMessage(String.valueOf(chatId), BotMessages.SUCCESSFUL_START_MESSAGE.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardFactory.createKeyboard(BotState.ON_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        return message;
    }

    public BotApiMethod<?> addNewPack(Long chatId) {
        CardPack cardPack = new CardPack();
        cardPackCache.saveCardPack(chatId, cardPack);
        SendMessage message = new SendMessage(String.valueOf(chatId), BotMessages.CREATING_CARD_PACK_NAME.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardFactory.createKeyboard(BotState.ON_PACK_CREATION_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        botStateCache.saveBotState(chatId, BotState.ON_PACK_CREATION_NAME);
        return message;
    }

    public BotApiMethod<?> addPackName(Long chatId, Message message) {
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);

        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            return getStartMessage(chatId);
        }

        Person person = serviceAggregator.getPersonService().getPersonById(chatId);
        if (person.getCardPacks().stream().anyMatch(x -> x.getName().equals(message.getText()))) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.EXCEPTION_PACK_ALREADY_EXIST.getAnswer()));
            return getStartMessage(chatId);
        }
        person.addCardPack(cardPack);
        cardPack.setName(message.getText());
        serviceAggregator.getCardPackService().addCardPack(cardPack);

        botStateCache.saveBotState(chatId, BotState.ON_START);
        SendMessage result = new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_PACK_ADD.getAnswer());
        result.setReplyMarkup(ReplyKeyboardFactory.createKeyboard(BotState.ON_START));
        return result;
    }


    public BotApiMethod<?> saveNewPerson(Message message) {
        Person person = new Person();
        person.setName(message.getFrom().getUserName());
        person.setId(message.getChatId());
        serviceAggregator.getPersonService().addPerson(person);
        return getStartMessage(message.getChatId());
    }

    public BotApiMethod<?> getPacksTable(Long chatId) {
        List<CardPack> cardPacks = serviceAggregator.getCardPackService().getPacksByPersonId(chatId);
        if (cardPacks.isEmpty()) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            return new SendMessage(chatId.toString(), BotMessages.EXCEPTION_NO_AVAILABLE_PACK.getAnswer());
        }

        if (tableCache.getTableNumberMap().get(chatId) == null) {
            tableCache.saveNumber(chatId, 1);
        }

        int maxTablesAmount = cardPacks.size() / (tableColumns * tableRows) + 1;
        int tableNumber = tableCache.getTableNumberMap().get(chatId);
        CardPack[][] rawTable = getCardPacks(cardPacks, tableNumber);

        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardFactory.createChooseTableKeyboard(rawTable, tableNumber, maxTablesAmount);
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_PACK_CHOOSE_TABLE.getAnswer());
        sendMessage.setReplyMarkup(keyboardMarkup);
        botStateCache.saveBotState(chatId, BotState.ON_CHOOSE_PACK);
        return sendMessage;
    }

    private CardPack[][] getCardPacks(List<CardPack> cardPacks, int tableNumber) {
        CardPack[][] rawTable = new CardPack[tableRows][tableColumns];
        for (int i = 0; i < tableRows; i++) {
            for (int j = 0; j < tableColumns; j++) {
                int index = i * tableColumns + j + (tableNumber - 1) * tableColumns * tableRows;
                rawTable[i][j] = index >= cardPacks.size() ? null : cardPacks.get(index);
            }
        }
        return rawTable;
    }


    public BotApiMethod<?> choosePack(Long chatId, Message message) {
        if (message.getText().equals("➡️")) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            tableCache.saveNumber(chatId, tableCache.getTableNumberMap().get(chatId) + 1);
            return getPacksTable(chatId);
        } else if (message.getText().equals("⬅️")) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            tableCache.saveNumber(chatId, tableCache.getTableNumberMap().get(chatId) - 1);
            return getPacksTable(chatId);
        } else if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            tableCache.saveNumber(chatId, null);
            return getStartMessage(chatId);
        }
        tableCache.saveNumber(chatId, null);
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        return getPackInfo(chatId, message.getText());
    }

    public BotApiMethod<?> getPackInfo(Long chatId, String packName) {
        CardPack cardPack = serviceAggregator.getCardPackService().getCardPackByName(packName);
        if (cardPack == null || !cardPack.getPerson().getId().equals(chatId)) {
            executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.EXCEPTION_PACK_WAS_NOT_FOUND.getAnswer()));
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            return getPacksTable(chatId);
        }

        SendMessage setKeyboard = new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_PACK_MENU.getAnswer());
        setKeyboard.setReplyMarkup(ReplyKeyboardFactory.createKeyboard(BotState.ON_PACK_INFO));
        executeAdditionalMethod(setKeyboard);

        cardPackCache.saveCardPack(chatId, cardPack);
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);

        SendMessage sendMessage = new SendMessage(chatId.toString(), cardPack.getName() + "\n\n" + "Pack info\n\n" +
                "Amount of cards: " + cardPack.getCards().size() + "\n" +
                "Wrong answers: " + "\n" +
                "Correct answers: " + "\n" +
                "Winrate: " + "\n");
        sendMessage.setReplyMarkup(InlineKeyboardFactory.createKeyboard(BotState.ON_PACK_INFO));
        return sendMessage;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
    public void setTableCache(TableCache tableCache) {
        this.tableCache = tableCache;
    }
}
