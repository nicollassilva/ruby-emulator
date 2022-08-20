package com.cometproject.server.game.commands.user.building;

public enum BuildingType {
    NONE(""),

    FILL("preenchimento"),
    COPY("copiar"),
    MOVE("mover"),

    ;

    private final String key;

    BuildingType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
