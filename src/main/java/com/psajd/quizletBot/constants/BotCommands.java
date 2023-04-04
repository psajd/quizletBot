package com.psajd.quizletBot.constants;

public enum BotCommands {
    START("/start"),
    NEW_PACK("New pack"),
    MY_PACKS("My packs"),

    CERTAIN_PACK("Choose pack");
    private final String command;

    BotCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
