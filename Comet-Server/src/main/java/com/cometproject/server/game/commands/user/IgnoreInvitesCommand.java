package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;

public class IgnoreInvitesCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] message) {
        final boolean ignoreInvites = !client.getPlayer().getSettings().getIgnoreInvites();

        client.getPlayer().getSettings().setIgnoreInvites(ignoreInvites);

        PlayerDao.saveIgnoreInvitations(ignoreInvites, client.getPlayer().getId());

        sendWhisper("Agora você está " + (ignoreInvites ? "ignorando" : "recebendo") + "convites!", client);
    }

    @Override
    public String getPermission() {
        return "ignore_invites_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.ignore_invites.description", "Ignorar convites.");
    }
}