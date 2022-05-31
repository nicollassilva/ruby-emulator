package com.cometproject.api.game.rooms;

import javax.annotation.Nullable;

public enum RoomDiagonalType {
    ENABLED(0, "diagonal irá funcionar como no habbo original."),
    ALLOW_ALL(1, "diagonal sem regras, simplesmente funciona."),
    DISABLED(2, "diagonal desativada."),
    ;

    private final String description;
    private final byte key;

    RoomDiagonalType(int key, String description) {
        this.key = (byte) key;
        this.description = description;
    }

    public static boolean isAllowed(byte key) {
        return RoomDiagonalType.ENABLED.key == key || RoomDiagonalType.ALLOW_ALL.key == key;
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
            case "enabled":
            case "ativa":
            case "ativar":
            case "on":
            case "0":
                return RoomDiagonalType.ENABLED;

            case "allow": // TODO: melhor nome kkk
            case "1":
                return RoomDiagonalType.ALLOW_ALL;

            case "disable":
            case "disabled":
            case "desativar":
            case "off":
            case "2":
                return RoomDiagonalType.DISABLED;
        }

        return null;
    }

    public String getDescription() {
        return description;
    }

    public byte getKey() {
        return key;
    }
}
