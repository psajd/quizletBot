package com.psajd.quizletBot.models;

import com.psajd.quizletBot.constants.BotCommands;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardFactory {

    public static InlineKeyboardMarkup createKeyboard(BotState state) {
        switch (state) {
            case ON_PACK_INFO -> {
                return onPackInfoKeyboard();
            }
            default -> {
                return null;
            }
        }
    }

    private static InlineKeyboardMarkup onPackInfoKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton addBtn = new InlineKeyboardButton(BotCommands.ADD_CARD.getCommand());
        addBtn.setCallbackData(BotCommands.ADD_CARD.getCommand());

        InlineKeyboardButton removeBtn = new InlineKeyboardButton(BotCommands.REMOVE_CARD.getCommand());
        addBtn.setCallbackData(BotCommands.REMOVE_CARD.getCommand());
        firstRow.add(addBtn);
        firstRow.add(removeBtn);

        inlineKeyboardMarkup.setKeyboard(List.of(firstRow));
        return inlineKeyboardMarkup;
    }
}
