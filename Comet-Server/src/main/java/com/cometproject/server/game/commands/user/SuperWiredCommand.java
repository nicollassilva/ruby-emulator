package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class SuperWiredCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

            client.getPlayer().getSession().send(new MotdNotificationMessageComposer(
                    "<b>Comandos do SuperWired no Ruby</b>\n" +
                    "- handitem: " + "Dá um handitem especial ao usuário afetado"
            ));
    }

    @Override
    public String getPermission() {
        return "superwired_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.superwired.description", "Revisa la lista de comandos disponibles en el superwired");
    }
}
