package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class VerClonesCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        String target = params[0];

        //Session targetPlayer = NetworkManager.getInstance().getSessions().getByPlayerUsername(target);

        String getRegIp = PlayerDao.getRegIpByUsername(target);

        String username = PlayerDao.getUsernameByRegIp(getRegIp);

        client.send(new MotdNotificationMessageComposer("Usuário: " + target + "\n\n" + "Fakes do usuário: " + username));
    }

    @Override
    public String getPermission() {
        return "verclones_command";
    }

    @Override
    public String getParameter() {
        return "(usuário)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.verclones.description", "Verifica as fake de um usuário");
    }
}
