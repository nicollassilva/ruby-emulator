package com.cometproject.server.game.commands.bot;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;

public class FreezeBotCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendNotif(Locale.get("command.botcontrol.none"), client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null)
            return;

        final String username = params[0];
        final BotEntity botEntity = room.getBots().getBotByName(username);

            if(botEntity.canWalk()) {
                botEntity.setCanWalk(false);
            } else {
                botEntity.setCanWalk(true);
            }
    }

    @Override
    public String getPermission() {
        return "freezebot_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
