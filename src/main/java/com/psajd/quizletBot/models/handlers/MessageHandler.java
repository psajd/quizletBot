package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotAnswers;
import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.BotStateCash;
import com.psajd.quizletBot.models.KeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
public class MessageHandler {

    private MainMenuHandler mainMenuHandler;
    private BotStateCash botStateCash;

    @Autowired
    public void setBotStateCash(BotStateCash botStateCash) {
        this.botStateCash = botStateCash;
    }

    @Autowired
    public MessageHandler(MainMenuHandler mainMenuHandler) {
        this.mainMenuHandler = mainMenuHandler;
    }

    public BotApiMethod<?> handle(Message message) {
        if (!message.hasText()) {
            return getErrorMessage(message.getChatId().toString());
        }

        BotApiMethod<?> method = null;


        return method == null ? getErrorMessage(message.getChatId().toString()) : method;
    }

    public SendMessage getErrorMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, BotAnswers.EXCEPTION_TRY_AGAIN.getAnswer());
        ReplyKeyboardMarkup replyKeyboardMarkup = KeyboardFactory.createKeyboard(BotState.ON_START);
        message.setReplyMarkup(replyKeyboardMarkup);
        return message;
    }
}
