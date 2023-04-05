package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.models.caching.BotStateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandler {

    private BotStateCache botStateCache;

    @Autowired
    public void setBotStateCash(BotStateCache botStateCache) {
        this.botStateCache = botStateCache;
    }

    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {

        return null;
    }
}
