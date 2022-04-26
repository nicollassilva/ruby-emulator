package com.cometproject.server.game.permissions.types;

import java.sql.ResultSet;
import java.sql.SQLException;


public class OverrideCommandPermission {
    private final String commandId;
    private final int playerId;
    private final int rankId;
    private final boolean enabled;

    public OverrideCommandPermission(ResultSet data) throws SQLException {
        this.commandId = data.getString("command_id");
        this.playerId = data.getInt("player_id");
        this.rankId = data.getInt("rank_id");
        this.enabled = data.getString("enabled").equals("1");
    }

    public String getCommandId() {
        return commandId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getRankId() { return rankId;}

    public boolean isEnabled() {
        return enabled;
    }
}
