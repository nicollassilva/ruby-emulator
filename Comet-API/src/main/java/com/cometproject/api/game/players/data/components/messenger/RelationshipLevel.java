package com.cometproject.api.game.players.data.components.messenger;

public enum RelationshipLevel {
    POOP(4),
    BOBBA(3),
    SMILE(2),
    HEART(1);

    private final int levelId;

    RelationshipLevel(int id) {
        this.levelId = id;
    }

    public int getLevelId() {
        return levelId;
    }
}
