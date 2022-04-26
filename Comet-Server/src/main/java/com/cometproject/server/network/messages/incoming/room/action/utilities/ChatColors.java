package com.cometproject.server.network.messages.incoming.room.action.utilities;

public enum ChatColors {
    RED("#d63031"),
    BLUE("#2980b9"),
    GREEN("#27ae60"),
    YELLOW("#f1c40f"),
    ORANGE("#d35400"),
    PINK("#e84393"),
    PURPLE("#8e44ad"),
    GRAY("#b2bec3");

    private final String color;

    ChatColors(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public static ChatColors getColorByName(String name) {
        for (ChatColors type : ChatColors.values()) {
            if (type.color.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
