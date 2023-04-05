package com.psajd.quizletBot.constants;

public enum BotAnswers {

    EXCEPTION_TRY_AGAIN("Don't understand, please, try again."),

    SUCCESSFUL_START_MESSAGE("Please, use main menu: "),

    SUCCESSFUL_ALL_PERSON_PACKS("All your card packs: "),

    ERROR_ALL_PERSON_PACKS("You haven't got any packs"),

    CREATING_CARD_PACK_NAME("Please input card pack name"),

    WRONG_CARD_PACK_NAME("Your card pack name is invalid, try to create again"),

    SUCCESSFUL_CARD_PACK_ADD("Card pack was added successfully"),

    SUCCESSFUL_CARD_PACK_CHOOSE_TABLE("Enter the name of the desired deck of cards"),

    CREATING_CARD_ADD_QUESTION("Please, enter card question"),
    CREATING_CARD_ADD_ANSWER("Please, enter card answer");

    private final String answer;

    BotAnswers(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
