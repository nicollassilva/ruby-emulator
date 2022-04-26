package com.cometproject.server.game.players.types;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerBanner {
    private final String bannerName;
    private final int playerId;
    private final boolean status;

    public PlayerBanner(ResultSet data) throws SQLException {
        this.bannerName = data.getString("banner_name");
        this.playerId = data.getInt("player_id");
        this.status = data.getString("status").equals("1");
    }

    public String getBannerName() {
        return bannerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isStatus() {
        return status;
    }
}
