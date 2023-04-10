package com.psajd.quizletBot.constants;

public enum BotMessages {

    EXCEPTION_PACK_ALREADY_EXIST("Pack with this name already exists"),
    EXCEPTION_CARD_WAS_NOT_FOUND("Card not found, try again"),
    EXCEPTION_NO_AVAILABLE_PACK("You haven't got any packs"),
    EXCEPTION_PACK_WAS_NOT_FOUND("Pack wasn't found"),
    EXCEPTION_NOT_UNIQUE_CARD_TERM("Card with this term already exists"),
    EXCEPTION_TRY_AGAIN("Don't understand, please, try again."),
    EXCEPTION_NO_AVAILABLE_CARDS("No available cards"),

    SUCCESSFUL_START_MESSAGE("Please, use main menu: "),
    SUCCESSFUL_CORRECT_ANSWER("Answer is correct✅"),
    SUCCESSFUL_WRONG_ANSWER("Answer is wrong❌. Correct answer: "),
    SUCCESSFUL_CARD_REMOVE("Card removed successfully"),
    SUCCESSFUL_CARD_PACK_REMOVE("Card pack removed successfully"),
    QUESTION_ARE_YOU_SURE("Are you sure?"),

    SUCCESSFUL_ALL_PERSON_PACKS("All your card packs: "),
    INFO_YOUR_CARDS("Cards from yours card pack"),

    ERROR_ALL_PERSON_PACKS("You haven't got any packs"),

    CREATING_CARD_PACK_NAME("Please input card pack name"),

    WRONG_CARD_PACK_NAME("Your card pack name is invalid, try to create again"),

    SUCCESSFUL_CARD_PACK_ADD("Card pack was added successfully"),
    SUCCESSFUL_CARD_PACK_NAME_UPDATE("Card pack name was updated successfully"),
    SUCCESSFUL_CARD_ADD("Card was added successfully"),

    SUCCESSFUL_CARD_PACK_CHOOSE_TABLE("Enter the name of the desired deck of cards"),
    SUCCESSFUL_CARD_PACK_MENU("Card pack menu"),
    ERROR_SOMETHING_WENT_WRONG("Something went wrong, try again"),

    CREATING_CARD_ADD_QUESTION("Please, enter card term"),
    CREATING_CARD_ADD_ANSWER("Please, enter card definition"),
    DELETING_CARD_ENTER_CARD_TERM("Please, enter card term");

    private final String answer;

    BotMessages(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
