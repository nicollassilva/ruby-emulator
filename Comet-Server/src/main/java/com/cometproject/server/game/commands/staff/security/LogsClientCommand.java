package com.cometproject.server.game.commands.staff.security;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class LogsClientCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final String state = params[0];

        switch (state) {
            default:
            case "on":
                client.getPlayer().setLogsClientStaff(true);
                client.send(new NotificationMessageComposer("generic", "Activaste los logs de client"));
                break;

            case "off":
                client.getPlayer().setLogsClientStaff(false);
                client.send(new NotificationMessageComposer("generic", "Desactivaste los logs de client"));
                break;
        }
    }

    @Override
    public String getPermission() {
        return "hotelalert_command";
    }

    @Override
    public String getParameter() {
        return "(on / off)";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.logsclient.description", "Logs de lo que se realice dentro del hotel en burbujas");
    }
}
