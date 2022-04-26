package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

import static com.cometproject.api.game.rooms.entities.RoomEntityStatus.LAY;
import static com.cometproject.api.game.rooms.entities.RoomEntityStatus.SIT;

public class StandCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if(client.getPlayer().getEntity().hasStatus(SIT)){
            client.getPlayer().getEntity().removeStatus(SIT);
        }

        if(client.getPlayer().getEntity().hasStatus(LAY)) {
            client.getPlayer().getEntity().removeStatus(LAY);
        }

        client.getPlayer().getEntity().markNeedsUpdate();
        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "stand_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.stand.description", "Parate en una sala");
    }
}
