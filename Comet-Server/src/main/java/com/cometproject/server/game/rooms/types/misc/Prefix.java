package com.cometproject.server.game.rooms.types.misc;

public class Prefix {
    private final int id;
    private final String prefix;

    public Prefix(int id, String prefix) {
        this.id = id;
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getId() {
        return id;
    }
}
