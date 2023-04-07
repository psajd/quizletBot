package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotAnswers;
import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.entities.Person;
import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.InlineKeyboardFactory;
import com.psajd.quizletBot.models.QuizletBot;
import com.psajd.quizletBot.models.caching.BotStateCache;
import com.psajd.quizletBot.models.ReplyKeyboardFactory;
import com.psajd.quizletBot.models.caching.CardCache;
import com.psajd.quizletBot.models.caching.CardPackCache;
import com.psajd.quizletBot.models.caching.TableCache;
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
    private CardCache cardCache;

    public BotApiMethod<?> getStartMessage(long chatId) {
        SendMessage message = new SendMessage(String.valueOf(chatId), BotAnswers.SUCCESSFUL_START_MESSAGE.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardFactory.createKeyboard(BotState.ON_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        return message;
    }

    public BotApiMethod<?> getAllPacks(long chatId, Message message) {
        if (!message.getText().equals(BotCommands.MY_PACKS.getCommand())) {
            return null;
        }
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

    public BotApiMethod<?> addNewPack(Long chatId) {
        CardPack cardPack = new CardPack();
        cardPackCache.saveCardPack(chatId, cardPack);
        SendMessage message = new SendMessage(String.valueOf(chatId), BotAnswers.CREATING_CARD_PACK_NAME.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardFactory.createKeyboard(BotState.ON_PACK_CREATION_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        botStateCache.saveBotState(chatId, BotState.ON_PACK_CREATION_NAME);
        return message;
    }

    public BotApiMethod<?> addPackName(Long chatId, Message message) {
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);

        if (message.getText().equals("Go back ⬇️")) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            return getStartMessage(chatId);
        }
        if (message.getText().isBlank()) {
            return new SendMessage(chatId.toString(), BotAnswers.WRONG_CARD_PACK_NAME.getAnswer());
        }

        Person person = serviceAggregator.getPersonService().getPersonById(chatId);
        if (person.getCardPacks().stream().anyMatch(x -> x.getName().equals(message.getText()))) {
            botStateCache.saveBotState(chatId, BotState.ON_START);
            executeAdditionalMethod(new SendMessage(chatId.toString(), "Pack with this name already exists"));
            return getStartMessage(chatId);
        }
        person.addCardPack(cardPack);
        cardPack.setName(message.getText());

        serviceAggregator.getCardPackService().addCardPack(cardPack);

        botStateCache.saveBotState(chatId, BotState.ON_START);
        SendMessage result = new SendMessage(chatId.toString(), BotAnswers.SUCCESSFUL_CARD_PACK_ADD.getAnswer());
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
            return new SendMessage(chatId.toString(), "You haven't got any packs");
        }
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

        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardFactory.createChooseTableKeyboard(rawTable, tableNumber, maxTablesAmount);
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotAnswers.SUCCESSFUL_CARD_PACK_CHOOSE_TABLE.getAnswer());
        sendMessage.setReplyMarkup(InlineKeyboardFactory.createKeyboard(BotState.ON_PACK_INFO));
        sendMessage.setReplyMarkup(keyboardMarkup);
        botStateCache.saveBotState(chatId, BotState.ON_CHOOSE_PACK);
        return sendMessage;
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
        } else if (message.getText().equals("Go back ⬇️")) {
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
            executeAdditionalMethod(new SendMessage(chatId.toString(), "Pack wasn't found"));
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            return getPacksTable(chatId);
        }

        SendMessage setKeyboard = new SendMessage(chatId.toString(), BotAnswers.SUCCESSFUL_CARD_PACK_MENU.getAnswer());
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

    private void executeAdditionalMethod(BotApiMethod<?> method) {
        QuizletBot quizletBot = applicationContext.getBean("springWebhookBot", QuizletBot.class);
        try {
            quizletBot.execute(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public BotApiMethod<?> choosePackInfoButton(Long chatId, Message message) {
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

    public BotApiMethod<?> startTraining(Long chatId, Message message) {

        return null;
    }

    public BotApiMethod<?> showCards(Long chatId, Message message) {

        return null;

    }

    public BotApiMethod<?> addNewCard(Long chatId, Message message) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotAnswers.CREATING_CARD_ADD_QUESTION.getAnswer());
        botStateCache.saveBotState(chatId, BotState.ON_ADD_CARD_TERM);
        return sendMessage;
    }

    public BotApiMethod<?> removeCard(Long chatId, Message message) {
        botStateCache.saveBotState(chatId, BotState.ON_CARD_NAME_CHOICE);
        return new SendMessage(chatId.toString(), BotAnswers.DELETING_CARD_ENTER_CARD_TERM.getAnswer());
    }

    public BotApiMethod<?> choiceCardAndRemove(Long chatId, Message message) {
        Card card = serviceAggregator.getCardService().findCardByTermAndPerson(chatId, message.getText());

        if (card == null) {
            return new SendMessage(chatId.toString(), "Card not found, try again");
        }

        serviceAggregator.getCardService().deleteCard(card);
        executeAdditionalMethod(new SendMessage(chatId.toString(), "card removed successfully"));
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
    }

    public BotApiMethod<?> addCardTerm(Long chatId, Message message) {
        Card card = new Card();
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);
        if (cardPack.getCards().stream().anyMatch(x -> x.getTerm().equals(message.getText()))) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            executeAdditionalMethod(new SendMessage(chatId.toString(), "Term like this already exists"));
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        card.setTerm(message.getText());
        botStateCache.saveBotState(chatId, BotState.ON_ADD_CARD_DEFINITION);
        cardCache.saveCard(chatId, card);
        return new SendMessage(chatId.toString(), BotAnswers.CREATING_CARD_ADD_ANSWER.getAnswer());
    }

    public BotApiMethod<?> addCardDefinition(Long chatId, Message message) {
        Card card = cardCache.getCardMap().get(chatId);
        if (card == null) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_TABLE);
            return new SendMessage(chatId.toString(), BotAnswers.ERROR_SOMETHING_WENT_WRONG.getAnswer());
        }

        card.setDefinition(message.getText());
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);
        cardPack.addCard(card);
        card.setNumber(cardPack.getCards().size());
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        serviceAggregator.getCardService().addCard(card);

        executeAdditionalMethod(new SendMessage(chatId.toString(), BotAnswers.SUCCESSFUL_CARD_PACK_ADD.getAnswer()));

        return getPackInfo(chatId, cardPack.getName());
    }

    public BotApiMethod<?> removeCardPack(long chatId, Message message) {

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
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setCardCache(CardCache cardCache) {
        this.cardCache = cardCache;
    }

    @Autowired
    public void setTableCache(TableCache tableCache) {
        this.tableCache = tableCache;
    }
}
