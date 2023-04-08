package com.psajd.quizletBot.constants;

public enum BotCommands {
    START("/start"),
    NEW_PACK("New card pack\uD83C\uDD95"),
    CERTAIN_PACK("Choose card pack\uD83D\uDD8A"),

    TRAIN("Let's practice\uD83D\uDCA1"),
    SHOW_CARD("Show cards\uD83D\uDD2C"),
    ADD_CARD("Add card✅"),
    REMOVE_CARD("Remove card❌"),
    CHANGE_NAME("Change pack name\uD83D\uDEE0"),
    SHOW_PACK_STATISTIC("Show statistics\uD83D\uDCCA"),
    REMOVE_CARD_PACK("Remove card pack❌"),
    CARD_PACK_REMOVE_YES("Yes✅"),
    CARD_PACK_REMOVE_NO("No❌"),

    GO_BACK("Go back ⬇️");
    private final String command;

    BotCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
