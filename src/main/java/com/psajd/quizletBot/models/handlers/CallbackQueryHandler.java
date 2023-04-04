package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.models.BotStateCash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CallbackQueryHandler {

    private BotStateCash botStateCash;

    @Autowired
    public void setBotStateCash(BotStateCash botStateCash) {
        this.botStateCash = botStateCash;
    }

    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        return null;
    }
}
