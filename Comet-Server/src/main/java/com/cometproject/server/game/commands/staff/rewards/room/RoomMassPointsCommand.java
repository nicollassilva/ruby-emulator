package com.cometproject.server.game.commands.staff.rewards.room;

import com.cometproject.server.config.Locale;

public class RoomMassPointsCommand extends RoomMassCurrencyCommand {
    @Override
    public String getPermission() {
        return "roommasspoints_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roommasspoints.description");
    }
}
