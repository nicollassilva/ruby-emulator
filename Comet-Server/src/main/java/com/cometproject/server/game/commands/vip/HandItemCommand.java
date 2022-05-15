package com.cometproject.server.game.commands.vip;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class HandItemCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.handitem.none", "You have to type :drink %number%"), client);
            return;
        }

        try {
            final int handItem = Integer.parseInt(params[0]);

            if (handItem > 0) {
                client.getPlayer().getEntity().carryItem(handItem, false);
            } else if (handItem == 0) {
                client.getPlayer().getEntity().carryItem(0);
            }
        } catch (Exception e) {
            sendNotif(Locale.getOrDefault("command.handitem.invalid", "Por favor, use somente números!"), client);
        }
    }

    @Override
    public String getPermission() {
        return Locale.get("handitem_command");
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.number", "(número)");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.handitem.description", "Obtém um item de mão");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
