package com.psajd.quizletBot.models;

import com.psajd.quizletBot.configs.BotConfig;
import com.psajd.quizletBot.constants.BotAnswers;
import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.models.caching.BotStateCache;
import com.psajd.quizletBot.models.handlers.CallbackQueryHandler;
import com.psajd.quizletBot.models.handlers.MessageHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Getter
public class QuizletBot extends SpringWebhookBot {

    private final BotConfig telegramConfig;
    private MessageHandler messageHandler;
    private CallbackQueryHandler callbackQueryHandler;

    private BotStateCache botStateCache;

    public QuizletBot(DefaultBotOptions options, SetWebhook setWebhook, String botToken, BotConfig botConfig) {
        super(options, setWebhook, botToken);
        telegramConfig = botConfig;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                return handleInputMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                return callbackQueryHandler.handle(update.getCallbackQuery());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private BotApiMethod<?> handleInputMessage(Message message) {
        BotState botState;
        String inputMsg = message.getText();

        if (!message.hasText()) {
            return new SendMessage(message.getChatId().toString(), BotAnswers.EXCEPTION_TRY_AGAIN.getAnswer());
        }
        if (inputMsg.equals(BotCommands.START.getCommand())) {
            botState = BotState.ON_START;
        } else if (inputMsg.equals(BotCommands.NEW_PACK.getCommand())) {
            botState = BotState.ON_PACK_CREATION_START;
        } else if (inputMsg.equals(BotCommands.CERTAIN_PACK.getCommand())) {
            botState = BotState.ON_PACK_TABLE;
        } else if (inputMsg.equals(BotCommands.MY_PACKS.getCommand())) {
            botState = BotState.ON_ALL_PACKS;
        } else {
            botState = botStateCache.getBotStateMap().get(message.getFrom().getId()) == null
                    ? BotState.ON_START
                    : botStateCache.getBotStateMap().get(message.getFrom().getId());
        }

        return messageHandler.handle(message, botState);
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public String getBotPath() {
        return telegramConfig.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return telegramConfig.getWebhookPath();
    }

    @Autowired
    public void setCallbackQueryHandler(CallbackQueryHandler callbackQueryHandler) {
        this.callbackQueryHandler = callbackQueryHandler;
    }

    @Autowired
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Autowired
    public void setBotStateCache(BotStateCache botStateCache) {
        this.botStateCache = botStateCache;
    }
}
