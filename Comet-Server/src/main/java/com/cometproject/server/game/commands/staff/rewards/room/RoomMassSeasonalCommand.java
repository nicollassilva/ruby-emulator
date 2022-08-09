package com.cometproject.server.game.commands.staff.rewards.room;

import com.cometproject.server.config.Locale;

public class RoomMassSeasonalCommand extends RoomMassCurrencyCommand {
    @Override
    public String getPermission() {
        return "roommassrubis_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roommass.seasonal.description");
    }
}
