package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class SummonCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.summon.none", "Quem você quer puxar?"), client);
            return;
        }

        final String username = params[0];

        if (!PlayerManager.getInstance().isOnline(username)) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        final Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (username == client.getPlayer().getEntity().getUsername()) {
            sendNotif(Locale.getOrDefault("command.user.himself", "Você não pode puxar a si mesmo."), client);
            return;
        }

        session.send(new AlertMessageComposer(Locale.get("command.summon.summoned").replace("%summoner%", client.getPlayer().getData().getUsername())));
        session.send(new RoomForwardMessageComposer(client.getPlayer().getEntity().getRoom().getId()));

        session.getPlayer().bypassRoomAuth(true);
        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "summon_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.summon.description");
    }
}
