package com.psajd.quizletBot.models.keyboards;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.models.bot.BotState;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyKeyboardFactory {

    public static ReplyKeyboardMarkup createKeyboard(BotState state) {
        switch (state) {
            case ON_PACK_CREATION_START -> {
                return onPackCreationKeyboard();
            }
            case ON_PACK_INFO -> {
                return onPackInfoKeyboard();
            }
            default -> {
                return onStartKeyboard();
            }
        }
    }

    public static ReplyKeyboardMarkup onPractice(List<Card> cards) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(cards.get(0).getTerm());
        firstRow.add(cards.get(1).getTerm());

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(cards.get(2).getTerm());
        secondRow.add(cards.get(3).getTerm());

        KeyboardRow goBackRow = new KeyboardRow();
        goBackRow.add(BotCommands.GO_BACK.getCommand());

        rows.add(firstRow);
        rows.add(secondRow);
        rows.add(goBackRow);

        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup onShowCards(List<Card> cards, Integer number) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> arrowsRow = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        if (number == 1) {
            keyboardRow.add("❌");
        } else {
            keyboardRow.add("⬅️");
        }
        keyboardRow.add(number + " / " + (cards.size() / 3 + 1));
        if (number == cards.size() / 3 + 1) {
            keyboardRow.add("❌");
        } else {
            keyboardRow.add("➡️");
        }

        KeyboardRow goBackRow = new KeyboardRow();
        goBackRow.add(BotCommands.GO_BACK.getCommand());

        arrowsRow.add(keyboardRow);
        arrowsRow.add(goBackRow);

        replyKeyboardMarkup.setKeyboard(arrowsRow);
        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup onPackInfoKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow first = new KeyboardRow();
        first.add(BotCommands.PRACTICE.getCommand());
        first.add(BotCommands.SHOW_CARDS.getCommand());
        keyboardRows.add(first);

        KeyboardRow last = new KeyboardRow();
        last.add(BotCommands.GO_BACK.getCommand());
        keyboardRows.add(last);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup onPackCreationKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(BotCommands.GO_BACK.getCommand());
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
        lastRow.add(BotCommands.GO_BACK.getCommand());
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
        // Add the second row to the keyboard
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
