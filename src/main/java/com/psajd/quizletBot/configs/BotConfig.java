package com.psajd.quizletBot.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotConfig {
    @Value("${telegrambot.webHookPath}")
    String webhookPath;
    @Value("${telegrambot.userName}")
    String botName;
    @Value("${telegrambot.botToken}")
    String botToken;
}
