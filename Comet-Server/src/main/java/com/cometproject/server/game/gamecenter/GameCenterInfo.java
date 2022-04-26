package com.cometproject.server.game.gamecenter;

import com.cometproject.server.game.players.types.Player;

public class GameCenterInfo {
    private final int gameId;
    private final String gameName;
    private final String gamePath;
    private final int roomId;
    //private String type;

    public GameCenterInfo(int gameId, String gameName, String gamePath, int roomId) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gamePath = gamePath;
        this.roomId = roomId;
        //this.type = type;
    }

    public int getGameId() {
        return this.gameId;
    }

    public String getGamecenterBackgroundColour() {
        return "";
    }

    public Boolean getGameStatus() {
        return false;
    }

    public int getGamesLeftCount(Player player) {
        return 0;
    }

    public String getGamecenterTextColour() {
        return "";
    }

    public String getGameName() {
        return this.gameName;
    }

    public String getGamePath() {
        return this.gamePath;
    }

    public int getGameRoomId() {
        return this.roomId;
    }

    //public String getType() { return this.type; }

    public void onPlayButton(int gameId, Player player) {
    }
}