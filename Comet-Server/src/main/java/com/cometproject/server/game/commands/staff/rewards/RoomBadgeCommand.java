package com.cometproject.server.game.commands.staff.rewards;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.sessions.Session;

public class RoomBadgeCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        final String badge = params[0];

        for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
            playerEntity.getPlayer().getInventory().addBadge(badge, true);
        }

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s enviou o emblema '%c' no quarto %r"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%c", badge)
                .replace("%r", client.getPlayer().getEntity().getRoom().getData().getName());
    }

    @Override
    public String getPermission() {
        return "roombadge_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.badge", "%badge%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roombadge.description");
    }

    @Override
    public String getLoggableDescription(){
        return this.logDesc;
    }

    @Override
    public boolean isLoggable(){
        return true;
    }
}
