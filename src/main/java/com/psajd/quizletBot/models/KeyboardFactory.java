package com.psajd.quizletBot.models;

import com.psajd.quizletBot.constants.BotCommands;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboardMarkup createKeyboard(BotState state) {
        switch (state) {
            case ON_START -> {
                return onStartKeyboard();
            }
            default -> {
                return null;
            }
        }
    }

    private static ReplyKeyboardMarkup onStartKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add(BotCommands.NEW_PACK.getCommand());
        row.add(BotCommands.CERTAIN_PACK.getCommand());
        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        // Set each button for the second line
        row.add(BotCommands.MY_PACKS.getCommand());
        // Add the second row to the keyboard
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
