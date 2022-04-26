package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.components.games.GameTeam;
import com.cometproject.server.network.messages.outgoing.room.avatar.DanceMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class DanceCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif("Debes poner el numero de baile deseado", client);
            return;
        }

        try {
            final int danceId = Integer.parseInt(params[0]);

            if(danceId < 1 || danceId > 4)
                return;

            final PlayerEntity entity = client.getPlayer().getEntity();

            isExecuted(client);

            entity.setDanceId(danceId);
            entity.getRoom().getEntities().broadcastMessage(new DanceMessageComposer(entity.getId(), danceId));

        } catch (Exception e) {
            sendNotif("Debes poner un numero de baile correcto!", client);
        }
    }

    @Override
    public String getPermission() {
        return "dance_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.number", "número 1-4");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.dance.description", "");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
