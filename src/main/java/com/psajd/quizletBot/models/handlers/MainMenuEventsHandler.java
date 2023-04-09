package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotMessages;
import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.entities.Person;
import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.InlineKeyboardFactory;
import com.psajd.quizletBot.models.QuizletBot;
import com.psajd.quizletBot.models.caching.*;
import com.psajd.quizletBot.models.ReplyKeyboardFactory;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MainMenuEventsHandler {

    private final int tableRows = 2;
    private final int tableColumns = 3;
    private ApplicationContext applicationContext;
    private ServiceAggregator serviceAggregator;
    private CardPackCache cardPackCache;
    private CardPageCache cardPageCache;
    private BotStateCache botStateCache;
    private TableCache tableCache;
    private CardCache cardCache;

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
        if (message.getText().isBlank()) {
            return new SendMessage(chatId.toString(), BotMessages.WRONG_CARD_PACK_NAME.getAnswer());
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
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_PACK_CHOOSE_TABLE.getAnswer());
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
        } else if (message.getText().equals(BotCommands.SHOW_CARDS.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_SHOW_CARDS);
            return showCards(chatId, message);
        } else if (message.getText().equals(BotCommands.PRACTICE.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_PRACTICE);
            return startTraining(chatId);
        }
        return null;
    }

    public BotApiMethod<?> startTraining(Long chatId) {
        List<Card> cards = cardPackCache.getCardPackMap().get(chatId).getCards().stream().toList();
        if (cards.size() < 4) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            executeAdditionalMethod(new SendMessage(chatId.toString(), "Minimal amount of cards for practice is 4, add some."));
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
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
        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        } else if (message.getText().equals(cardCache.getCardMap().get(chatId).getTerm())) {
            executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CORRECT_ANSWER.getAnswer()));
            return startTraining(chatId);
        } else {
            executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_WRONG_ANSWER.getAnswer()
                    + cardCache.getCardMap().get(chatId).getTerm()));
            return startTraining(chatId);
        }
    }

    public BotApiMethod<?> showCards(Long chatId, Message message) {
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);
        if (cardPack.getCards().isEmpty()) {
            cardPageCache.saveCardPage(chatId, 1);
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        if (cardPageCache.getCardPageMap().get(chatId) == null) {
            cardPageCache.saveCardPage(chatId, 1);
        }

        if (!message.getText().equals(BotCommands.SHOW_CARDS.getCommand())) {
            if (message.getText().equals("⬅️")) {
                cardPageCache.saveCardPage(chatId, cardPageCache.getCardPageMap().get(chatId) - 1);
            } else if (message.getText().equals("➡️")) {
                cardPageCache.saveCardPage(chatId, cardPageCache.getCardPageMap().get(chatId) + 1);
            } else if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
                cardPageCache.saveCardPage(chatId, 1);
                tableCache.saveNumber(chatId, null);
                botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
                return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
            } else {
                return new SendMessage(message.getChatId().toString(), BotMessages.EXCEPTION_TRY_AGAIN.getAnswer());
            }
        }

        int cardsAmount = 3;

        List<Card> cards = cardPack.getCards().stream().toList();
        int pageIndex = cardPageCache.getCardPageMap().get(chatId) - 1;
        List<String> resultList = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.INFO_YOUR_CARDS.getAnswer());
        sendMessage.setReplyMarkup(ReplyKeyboardFactory.onShowCards(cards, pageIndex + 1));

        for (int i = 0; i < cardsAmount; i++) {
            Card card = cards.size() - (pageIndex * 3 + i) > 0 ? cards.get(pageIndex * 3 + i) : null;
            if (card != null) {
                String s = card.getTerm() + " - " + card.getDefinition();
                resultList.add(s);
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < resultList.size(); i++) {
            String s = resultList.get(i);
            result.append("*****\n");
            result.append(pageIndex * 3 + i + 1).append(". ");
            result.append(s);
            result.append("\n*****\n\n");
        }
        executeAdditionalMethod(sendMessage);
        return new SendMessage(chatId.toString(), result.toString());
    }

    public BotApiMethod<?> addNewCard(Long chatId, Message message) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.CREATING_CARD_ADD_QUESTION.getAnswer());
        botStateCache.saveBotState(chatId, BotState.ON_ADD_CARD_TERM);
        return sendMessage;
    }

    public BotApiMethod<?> removeCard(Long chatId, Message message) {
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
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        if (card == null) {
            return new SendMessage(chatId.toString(), BotMessages.EXCEPTION_CARD_WAS_NOT_FOUND.getAnswer());
        }

        serviceAggregator.getCardService().deleteCard(card);
        executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_REMOVE.getAnswer()));
        botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
        return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
    }

    public BotApiMethod<?> addCardTerm(Long chatId, Message message) {
        if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
            cardCache.saveCard(chatId, null);
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }
        Card card = new Card();
        CardPack cardPack = cardPackCache.getCardPackMap().get(chatId);
        if (cardPack.getCards().stream().anyMatch(x -> x.getTerm().equals(message.getText()))) {
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.EXCEPTION_NOT_UNIQUE_CARD_TERM.getAnswer()));
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
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
            return getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
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

        executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_ADD.getAnswer()));

        return getPackInfo(chatId, cardPack.getName());
    }

    public BotApiMethod<?> removeCardPackQuestion(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.QUESTION_ARE_YOU_SURE.getAnswer());
        sendMessage.setReplyMarkup(InlineKeyboardFactory.createKeyboard(BotState.ON_REMOVE_CARD_PACK));
        return sendMessage;
    }

    public BotApiMethod<?> removeCardPack(Long chatId) {
        serviceAggregator.getCardPackService().deleteCardPack(cardPackCache.getCardPackMap().get(chatId));
        executeAdditionalMethod(new SendMessage(chatId.toString(), BotMessages.SUCCESSFUL_CARD_PACK_REMOVE.getAnswer()));
        botStateCache.saveBotState(chatId, BotState.ON_START);
        return getStartMessage(chatId);
    }

    private List<Card> pickNRandomCards(List<Card> lst, int n) {
        List<Card> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
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
    public void setCardPageCache(CardPageCache cardPageCache) {
        this.cardPageCache = cardPageCache;
    }

    @Autowired
    public void setTableCache(TableCache tableCache) {
        this.tableCache = tableCache;
    }
}
