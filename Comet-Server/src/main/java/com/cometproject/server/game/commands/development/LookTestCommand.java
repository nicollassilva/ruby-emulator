package com.cometproject.server.game.commands.development;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class LookTestCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

        client.getPlayer().getSession().send(new NotificationMessageComposer("looks/figure/" + client.getPlayer().getData().getUsername(),
                Locale.getOrDefault("player.online", "%username% está conectado!")
                        .replace("%username%", client.getPlayer().getData().getUsername())));

        isExecuted(client);
    }

    @Override
    public String getPermission() {
        return "about_command";
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
