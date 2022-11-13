package com.cometproject.api.game.rooms;

import javax.annotation.Nullable;

public enum RoomProcessingType {
    DEFAULT(0, "padrao", "Nitro padrão (ordem de entrada)"),
    RANDOM(1, "aleatorio", "Nitro aleatório (antigo futnitro)"),
    CLICK(2, "futebol", "Nitro original do futebol 2013."),
    ;

    private final String description;
    private final byte key;
    private final String name;

    RoomProcessingType(int key, String name, String description) {
        this.key = (byte) key;
        this.name = name;
        this.description = description;
    }

    public static RoomProcessingType parse(String str) {
        switch (str.toLowerCase()) {

            case "rnd":
            case "random":
            case "futnitro":
            case "aleatorio":
            case "1":
                return RoomProcessingType.RANDOM;

            case "click":
            case "habb":
            case "fut":
            case "on":
            case "2":
                return RoomProcessingType.CLICK;


            default:
                return RoomProcessingType.DEFAULT;
        }


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
