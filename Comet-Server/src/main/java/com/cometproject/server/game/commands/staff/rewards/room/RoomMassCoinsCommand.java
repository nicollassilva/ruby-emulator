package com.cometproject.server.game.commands.staff.rewards.room;

import com.cometproject.server.config.Locale;

public class RoomMassCoinsCommand extends RoomMassCurrencyCommand {
    @Override
    public String getPermission() {
        return "roommasscoins_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roommasscoins.description");
    }
}
