package com.cometproject.server.game.permissions.types;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CommandPermission {
    private final String commandId;
    private final int minimumRank;
    private final boolean vipOnly;
    private final boolean rightsOnly;
    private final RIGHTS_OVERRIDE override;

    public CommandPermission(ResultSet data) throws SQLException {
        this.commandId = data.getString("command_id");
        this.minimumRank = data.getInt("minimum_rank");
        this.vipOnly = data.getString("vip_only").equals("1");
        this.rightsOnly = data.getString("rights_only").equals("1");
        this.override = RIGHTS_OVERRIDE.valueOf(data.getString("rights_override"));
    }

    public String getCommandId() {
        return commandId;
    }

    public int getMinimumRank() {
        return minimumRank;
    }

    public boolean isVipOnly() {
        return vipOnly;
    }

    public boolean isRightsOnly() {
        return this.rightsOnly;
    }

    public RIGHTS_OVERRIDE getOverride() {
        return this.override;
    }

    public enum RIGHTS_OVERRIDE {
        NONE(0),
        RIGHTS(1),
        OWNER(2);

        private final int id;

        RIGHTS_OVERRIDE(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String toString(){
            switch(this.id) {
                case 0: return "NONE";
                case 1: return "RIGHTS";
                case 2: return "OWNER";
            }
            return "";
        }
    }
}
