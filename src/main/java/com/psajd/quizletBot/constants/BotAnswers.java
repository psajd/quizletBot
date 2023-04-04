package com.psajd.quizletBot.constants;

public enum BotAnswers {

    EXCEPTION_TRY_AGAIN("Don't understand, please, try again."),
    SUCCESSFUL_START_MESSAGE("Hi, i'm a quizlet bot." +
            "\nI will try to help you remember a lot of information easily"),

    SUCCESSFUL_ALL_PERSON_PACKS("All your card packs: "),
    ERROR_ALL_PERSON_PACKS("You haven't got any packs");
    private String answer;

    BotAnswers(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
