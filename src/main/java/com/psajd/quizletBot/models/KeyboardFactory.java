package com.psajd.quizletBot.models;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.CardPack;
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
            case ON_PACK_CREATION_START -> {
                return onPackCreation();
            }
            default -> {
                return null;
            }
        }
    }

    private static ReplyKeyboardMarkup onPackCreation() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add("Go back ⬇️");
        keyboardRows.add(keyboardButtons);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup createChooseTableKeyboard(CardPack[][] cardPacks, int tableNumber, int maxTablesAmount) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (CardPack[] row : cardPacks) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (CardPack element : row) {
                keyboardRow.add(element == null ? " " : element.getName());
            }
            keyboardRows.add(keyboardRow);
        }
        KeyboardRow keyboardRow = new KeyboardRow();
        if (tableNumber == 1) {
            keyboardRow.add("❌");
        } else {
            keyboardRow.add("⬅️");
        }
        keyboardRow.add(tableNumber + " / " + maxTablesAmount);
        if (tableNumber == maxTablesAmount) {
            keyboardRow.add("❌");
        } else {
            keyboardRow.add("➡️");
        }
        keyboardRows.add(keyboardRow);
        KeyboardRow lastRow = new KeyboardRow();
        lastRow.add("Go back ⬇️");
        keyboardRows.add(lastRow);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
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
