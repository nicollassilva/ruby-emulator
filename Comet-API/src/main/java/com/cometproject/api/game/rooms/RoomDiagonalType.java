package com.cometproject.api.game.rooms;

import javax.annotation.Nullable;

public enum RoomDiagonalType {
    DISABLED(0, "desativada", "diagonal desativada."),
    STRICT(1, "strict", "diagonal irá funcionar como no habbo original."),
    ENABLED(2, "ativa", "diagonal sem regras, simplesmente funciona."),
    ;

    private final String description;
    private final byte key;
    private final String name;

    RoomDiagonalType(int key, String name, String description) {
        this.key = (byte) key;
        this.name = name;
        this.description = description;
    }

    public static boolean isAllowed(byte key) {
        return RoomDiagonalType.STRICT.key == key || RoomDiagonalType.ENABLED.key == key;
    }

    public static RoomDiagonalType toggle(RoomDiagonalType type) {
        switch (type) {
            case ENABLED:
                return DISABLED;

            case DISABLED:
                return ENABLED;

            default:
                return type;
        }
    }

    @Nullable
    public static RoomDiagonalType parse(String str) {
        switch (str.toLowerCase()) {
            case "disable":
            case "disabled":
            case "desativar":
            case "off":
            case "0":
                return RoomDiagonalType.DISABLED;

            case "strict":
            case "1":
                return RoomDiagonalType.STRICT; // sim, retornamos algo com outro nome, porque os players são burros...
            // strict apenas para internamente sabermos que é como no habbo.com.br

            case "enabled":
            case "enable":
            case "ativar":
            case "ativa":
            case "on":
            case "2":
                return RoomDiagonalType.ENABLED;
        }

        return null;
    }

    public String getDescription() {
        return description;
    }

    public byte getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
