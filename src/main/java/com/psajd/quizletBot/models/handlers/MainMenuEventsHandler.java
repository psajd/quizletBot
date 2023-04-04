package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotAnswers;
import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.entities.Person;
import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.caching.BotStateCash;
import com.psajd.quizletBot.models.KeyboardFactory;
import com.psajd.quizletBot.models.caching.CardPackCash;
import com.psajd.quizletBot.repositories.PersonRepository;
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
    private ServiceAggregator serviceAggregator;
    private CardPackCash cardPackCash;
    private BotStateCash botStateCash;

    public SendMessage getStartMessage(long chatId, Message inputMessage) {
        SendMessage message = new SendMessage(String.valueOf(chatId), inputMessage.getText().equals(BotCommands.START.getCommand()) ?
                BotAnswers.SUCCESSFUL_START_MESSAGE.getAnswer() : BotAnswers.EXCEPTION_TRY_AGAIN.getAnswer());
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
        cardPackCash.saveCardPack(chatId, cardPack);
        SendMessage message = new SendMessage(String.valueOf(chatId), BotAnswers.CREATING_CARD_PACK_NAME.getAnswer());

        botStateCash.saveBotState(chatId, BotState.ON_PACK_CREATION_NAME);
        return message;
    }

    public SendMessage addPackName(Long chatId, Message message) {
        CardPack cardPack = cardPackCash.getCardPackMap().get(chatId);

        if (message.getText().isBlank()) {
            return new SendMessage(chatId.toString(), BotAnswers.WRONG_CARD_PACK_NAME.getAnswer());
        }

        Person person = serviceAggregator.getPersonService().getPersonById(chatId);
        person.addCardPack(cardPack);
        cardPack.setName(message.getText());

        serviceAggregator.getCardPackService().updateCardPack(cardPack);

        botStateCash.saveBotState(chatId, BotState.ON_START);
        return new SendMessage(chatId.toString(), BotAnswers.SUCCESSFUL_CARD_PACK_ADD.getAnswer());
    }

    public BotApiMethod<?> saveNewPerson(Message message) {
        Person person = new Person();
        person.setName(message.getFrom().getUserName());
        person.setId(message.getChatId());
        serviceAggregator.getPersonService().addPerson(person);
        return getStartMessage(message.getChatId(), message);
    }

    @Autowired
    public void setServiceAggregator(ServiceAggregator serviceAggregator) {
        this.serviceAggregator = serviceAggregator;
    }

    @Autowired
    public void setBotStateCash(BotStateCash botStateCash) {
        this.botStateCash = botStateCash;
    }

    @Autowired
    public void setCardPackCash(CardPackCash cardPackCash) {
        this.cardPackCash = cardPackCash;
    }
}
