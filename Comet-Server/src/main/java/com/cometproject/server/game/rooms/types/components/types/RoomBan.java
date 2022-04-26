package com.cometproject.server.game.rooms.types.components.types;

public class RoomBan {
    private final int playerId;
    private final String playerName;
    private final int expireTimestamp;

    private final boolean isPermanent;

    public RoomBan(int playerId, String playerName, int expireTimestamp) {
        this.playerId = playerId;
        this.expireTimestamp = expireTimestamp;
        this.playerName = playerName;

        this.isPermanent = this.expireTimestamp == -1;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getExpireTimestamp() {
        return expireTimestamp;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public String getPlayerName() {
        return playerName;
    }
}
