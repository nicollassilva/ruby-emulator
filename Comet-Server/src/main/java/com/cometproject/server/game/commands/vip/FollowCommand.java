package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class FollowCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendWhisper(Locale.getOrDefault("command.follow.none", "Who you want to follow?"), client);
            return;
        }

        final Session leader = NetworkManager.getInstance().getSessions().getByPlayerUsername(params[0]);

        if (leader == client) {
            sendNotif(Locale.getOrDefault("command.follow.playerhimself", "You can't follow yourself!"), client);
            return;
        }


        if (leader != null && leader.getPlayer() != null && leader.getPlayer().getEntity() != null) {
            if (!leader.getPlayer().getSettings().getAllowFollow() && !client.getPlayer().getPermissions().getRank().modTool()) {
                sendNotif(Locale.getOrDefault("command.follow.disabled", "This user has follow disabled."), client);
                return;
            }

            isExecuted(client);
            client.send(new RoomForwardMessageComposer(leader.getPlayer().getEntity().getRoom().getId()));
        } else {
            if (leader == null || leader.getPlayer() == null)
                sendNotif(Locale.get("command.follow.online"), client);
            else
                sendNotif(Locale.get("command.follow.room"), client);
        }
    }

    @Override
    public String getPermission() {
        return "follow_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuario)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.follow.description", "Sigue a un usuario a la sala en la que esté");
    }
}
