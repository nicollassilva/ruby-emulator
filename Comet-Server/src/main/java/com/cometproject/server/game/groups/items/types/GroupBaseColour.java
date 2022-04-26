package com.cometproject.server.game.groups.items.types;

import com.cometproject.api.game.groups.items.IGroupBadgeItem;

import java.sql.ResultSet;
import java.sql.SQLException;


public class GroupBaseColour implements IGroupBadgeItem {
    private final int id;
    private final String colour;

    public GroupBaseColour(ResultSet data) throws SQLException {
        this.id = data.getInt("id");
        this.colour = data.getString("firstvalue");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getFirstValue() {
        return colour;
    }

    @Override
    public String getSecondValue() {
        return null;
    }
}
