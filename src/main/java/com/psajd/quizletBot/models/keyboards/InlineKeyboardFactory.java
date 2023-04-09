package com.psajd.quizletBot.models.keyboards;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.models.bot.BotState;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardFactory {

    public static InlineKeyboardMarkup createKeyboard(BotState state) {
        switch (state) {
            case ON_PACK_INFO -> {
                return onPackInfoKeyboard();
            }
            case ON_REMOVE_CARD_PACK -> {
                return onRemoveCardPack();
            }
            default -> {
                return null;
            }
        }
    }

    private static InlineKeyboardMarkup onRemoveCardPack() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonYes = new InlineKeyboardButton();
        InlineKeyboardButton buttonNo = new InlineKeyboardButton();


        buttonYes.setText(BotCommands.CARD_PACK_REMOVE_YES.getCommand());
        buttonYes.setCallbackData(BotCommands.CARD_PACK_REMOVE_YES.getCommand());

        buttonNo.setText(BotCommands.CARD_PACK_REMOVE_NO.getCommand());
        buttonNo.setCallbackData(BotCommands.CARD_PACK_REMOVE_NO.getCommand());


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonYes);
        keyboardButtonsRow1.add(buttonNo);

        List<List<InlineKeyboardButton>> res = new ArrayList<>();
        res.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(res);
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardMarkup onPackInfoKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonAdd = new InlineKeyboardButton();
        buttonAdd.setText(BotCommands.ADD_CARD.getCommand());

        InlineKeyboardButton buttonRemove = new InlineKeyboardButton();
        buttonRemove.setText(BotCommands.REMOVE_CARD.getCommand());

        InlineKeyboardButton buttonChangeName = new InlineKeyboardButton();
        buttonChangeName.setText(BotCommands.CHANGE_NAME.getCommand());

       /* InlineKeyboardButton buttonStatistics = new InlineKeyboardButton();
        buttonStatistics.setText(BotCommands.SHOW_PACK_STATISTIC.getCommand());*/

        InlineKeyboardButton buttonRemoveCardPack = new InlineKeyboardButton();
        buttonRemoveCardPack.setText(BotCommands.REMOVE_CARD_PACK.getCommand());


        buttonAdd.setCallbackData(BotCommands.ADD_CARD.getCommand());
        buttonRemove.setCallbackData(BotCommands.REMOVE_CARD.getCommand());
        buttonChangeName.setCallbackData(BotCommands.CHANGE_NAME.getCommand());
        //buttonStatistics.setCallbackData(BotCommands.SHOW_PACK_STATISTIC.getCommand());
        buttonRemoveCardPack.setCallbackData(BotCommands.REMOVE_CARD_PACK.getCommand());

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonAdd);
        keyboardButtonsRow1.add(buttonRemove);
        keyboardButtonsRow2.add(buttonChangeName);
        //keyboardButtonsRow3.add(buttonStatistics);
        keyboardButtonsRow4.add(buttonRemoveCardPack);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
