package com.cometproject.server.utilities;

import com.cometproject.server.network.messages.incoming.room.action.utilities.ChatColors;

public class MessageColorUtil {
    public static MessageColorUtil instance;

    public MessageColorUtil() {
        instance = null;
    }

    public static MessageColorUtil getInstance() {
        if(instance == null) {
            instance = new MessageColorUtil();
        }

        return instance;
    }

    public String getFilteredString(String targetString) {
        final String color = this.getFirstColorCodeOnString(targetString);

        if(color.isEmpty())
            return targetString;

        return "<font color=\"" + color + "\">" + this.clearColorsCode(targetString) + "</font>";
    }

    public String getFirstColorCodeOnString(String targetString) {
        if(targetString.contains("@vermelho@")) {
            return ChatColors.RED.getColor();
        }

        if(targetString.contains("@azul@")) {
            return ChatColors.BLUE.getColor();
        }

        if(targetString.contains("@verde@")) {
            return ChatColors.GREEN.getColor();
        }

        if(targetString.contains("@amarelo@")) {
            return ChatColors.YELLOW.getColor();
        }

        if(targetString.contains("@laranja@")) {
            return ChatColors.ORANGE.getColor();
        }

        if(targetString.contains("@rosa@")) {
            return ChatColors.PINK.getColor();
        }

        if(targetString.contains("@roxo@")) {
            return ChatColors.PURPLE.getColor();
        }

        if(targetString.contains("@cinza@")) {
            return ChatColors.GRAY.getColor();
        }

        return "";
    }

    public String clearColorsCode(String targetString) {
        return targetString.replace("@vermelho@", "")
                .replace("@azul@", "")
                .replace("@amarelo@", "")
                .replace("@laranja@", "")
                .replace("@verde@", "")
                .replace("@cinza@", "")
                .replace("@roxo@", "")
                .replace("@rosa@", "");
    }
}
