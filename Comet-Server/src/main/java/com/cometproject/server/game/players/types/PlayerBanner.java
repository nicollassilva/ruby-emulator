package com.cometproject.server.game.players.types;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerBanner {
    private final String bannerName;
    private final boolean status;

    public PlayerBanner(ResultSet data) throws SQLException {
        this.bannerName = data.getString("banner_name");
        this.status = data.getString("status").equals("1");
    }

    public String getBannerName() {
        return bannerName;
    }

    public boolean isEnabled() {
        return status;
    }
}
