package com.cometproject.server.game.players.data;

public class GamePlayer {

    private final int id;
    private final String username;
    private final String figure;
    private final String gender;
    private final int points;
    private final int gameId;

    public GamePlayer(int id, String username, String figure, String gender, int points, int gameId) {
        this.id = id;
        this.username = username;
        this.figure = figure;
        this.gender = gender;
        this.points = points;
        this.gameId = gameId;
    }

    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public String getUsername() {
        return username;
    }

    public String getFigure() {
        return figure;
    }

    public String getGender() {
        return gender;
    }

    public int getGame() {
        return gameId;
    }
}
