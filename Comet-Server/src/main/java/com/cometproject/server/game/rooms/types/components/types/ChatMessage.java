package com.cometproject.server.game.rooms.types.components.types;

public class ChatMessage {
    private final int playerId;
    private final String message;

    public ChatMessage(int userId, String message) {
        this.playerId = userId;
        this.message = message;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getMessage() {
        return message;
    }
}
