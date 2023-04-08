package com.psajd.quizletBot.models;

public enum BotState {
    ON_START,
    ON_PACK_CREATION_START,
    ON_PACK_CREATION_NAME,
    ON_PACK_TABLE,
    ON_ALL_PACKS,
    ON_CHOOSE_PACK,
    ON_PACK_INFO,
    ON_PRACTICE,
    ON_SHOW_CARDS,
    ON_ADD_CARD,
    ON_ADD_CARD_TERM,
    ON_ADD_CARD_DEFINITION,
    ON_REMOVE_CARD,
    ON_REMOVE_CARD_PACK,
    ON_CARD_NAME_CHOICE;
}
