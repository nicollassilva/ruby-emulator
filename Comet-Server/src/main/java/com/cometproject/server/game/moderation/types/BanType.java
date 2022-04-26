package com.cometproject.server.game.moderation.types;

public enum BanType {
    IP,
    USER,
    TRADE,
    MACHINE;

    public static BanType getType(String type) {
        return type.equals("ip") ? IP : type.equals("user") ? USER : type.equals("trade") ? TRADE : MACHINE;
    }
}
