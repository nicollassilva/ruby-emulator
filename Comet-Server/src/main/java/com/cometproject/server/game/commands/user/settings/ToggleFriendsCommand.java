package com.cometproject.server.game.commands.user.settings;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class ToggleFriendsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getSettings().getAllowFriendRequests()) {
            client.getPlayer().getSettings().setAllowFriendRequests(false);
            sendNotif(Locale.getOrDefault("command.togglefriends.disabled", "Você desativou as solicitações de amizade."), client);
        } else {
            client.getPlayer().getSettings().setAllowFriendRequests(true);
            sendNotif(Locale.getOrDefault("command.togglefriends.enabled", "Você ativou solicitações de amizade."), client);

        }

        PlayerDao.saveAllowFriendRequests(client.getPlayer().getSettings().getAllowFriendRequests(), client.getPlayer().getId());
    }

    @Override
    public String getPermission() {
        return "togglefriends_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.togglefriends.description", "Activa o desactiva las solicitudes de amistad");
    }
}
