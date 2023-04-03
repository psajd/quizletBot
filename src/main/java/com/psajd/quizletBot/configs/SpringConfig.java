package com.psajd.quizletBot.configs;

import com.psajd.quizletBot.models.QuizletBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
public class SpringConfig {
    private final BotConfig telegramConfig;

    @Autowired
    public SpringConfig(BotConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public QuizletBot springWebhookBot(SetWebhook setWebhook) {
        return new QuizletBot(new DefaultBotOptions(),setWebhook, telegramConfig.getBotToken(), telegramConfig);
    }
}
