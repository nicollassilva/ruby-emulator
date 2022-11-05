package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class FutnitroCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
                sendNotif(Locale.getOrDefault("command.need.rights", "You need rights to use this command!"), client);
                return;
            }

            if (client.getPlayer().getEntity().getRoom().hasAttribute("futnitro")) {
                client.getPlayer().getEntity().getRoom().removeAttribute("futnitro");
                sendNotif(Locale.getOrDefault("command.futnitro.disabled", "Futnitro desativado!"), client);
            } else {
                client.getPlayer().getEntity().getRoom().setAttribute("futnitro", "1");
                sendNotif(Locale.getOrDefault("command.futnitro.enabled", "Futnitro ativado!"), client);
            }

        }
    }

    @Override
    public String getPermission() {
        return "futnitro_command";
    }


    @Override
    public String getParameter() {
        return null;
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.futnitro.description", "Ativar/destaivar o nitro aleatório");
    }
}
