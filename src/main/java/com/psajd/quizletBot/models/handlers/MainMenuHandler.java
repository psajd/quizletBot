package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotAnswers;
import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.KeyboardFactory;
import com.psajd.quizletBot.models.QuizletBot;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

@Component
public class MainMenuHandler {
    private ServiceAggregator serviceAggregator;

    @Autowired
    public void setServiceAggregator(ServiceAggregator serviceAggregator) {
        this.serviceAggregator = serviceAggregator;
    }

    public BotApiMethod<?> handle(Message message) {
        String chatId = message.getChatId().toString();
        if (message.getText().equals(BotCommands.START.getCommand())) {
            return getStartMessage(chatId);
        } else if (message.getText().equals(BotCommands.MY_PACKS.getCommand())) {
            return getAllPacks(chatId);
        } else if (message.getText().equals(BotCommands.NEW_PACK.getCommand())) {

        } else if (message.getText().equals(BotCommands.CERTAIN_PACK.getCommand())) {

        }
        return null;
    }

    public SendMessage getStartMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, BotAnswers.SUCCESSFUL_START_MESSAGE.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = KeyboardFactory.createKeyboard(BotState.ON_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        return message;
    }

    public SendMessage getAllPacks(String chatId) {
        List<CardPack> cardPacks = serviceAggregator
                .getCardPackService()
                .getPacksByPersonId(Long.parseLong(chatId));
        StringBuilder stringBuilder = new StringBuilder();
        if (cardPacks.isEmpty()) {
            stringBuilder.append(BotAnswers.ERROR_ALL_PERSON_PACKS.getAnswer());
        } else {
            stringBuilder.append(BotAnswers.SUCCESSFUL_ALL_PERSON_PACKS.getAnswer());
            for (int i = 0; i < cardPacks.size(); i++) {
                CardPack cardPack = cardPacks.get(i);
                stringBuilder.append(i);
                stringBuilder.append("Card pack name: ").append(cardPack.getName());
                stringBuilder.append("Cards amount: ").append(cardPack.getCards().size());
                stringBuilder.append("\n");
            }
        }
        SendMessage message = new SendMessage(chatId, stringBuilder.toString());
        return message;
    }

    public SendMessage addNewPack(String chatId) {
        SendMessage message = new SendMessage(chatId, "");
        return message;
    }
}
