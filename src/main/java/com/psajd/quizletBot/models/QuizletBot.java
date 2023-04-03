package com.psajd.quizletBot.models;

import com.psajd.quizletBot.configs.BotConfig;
import lombok.Getter;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Getter
public class QuizletBot extends SpringWebhookBot {

    private final BotConfig telegramConfig;

    public QuizletBot(DefaultBotOptions options, SetWebhook setWebhook, String botToken, BotConfig botConfig) {
        super(options, setWebhook, botToken);
        telegramConfig = botConfig;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return new SendMessage(update.getMessage().getChatId().toString(), update.getMessage().getText());
    }

    @Override
    public String getBotPath() {
        return telegramConfig.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return telegramConfig.getWebhookPath();
    }
}
