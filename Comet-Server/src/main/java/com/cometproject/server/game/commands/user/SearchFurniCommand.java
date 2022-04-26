package com.cometproject.server.game.commands.user;

import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class SearchFurniCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if(params.length < 1)
            return;

        String mode = params[0];

        switch(mode) {
            case "activar":
                client.getPlayer().setIsSearchFurni(true);
                client.getPlayer().getSession().send(new NotificationMessageComposer("", "Has activado el modo de buscar furni satisfactoriamente"));
                break;

            case "desactivar":
                client.getPlayer().setIsSearchFurni(false);
                client.getPlayer().getSession().send(new NotificationMessageComposer("", "Has desactivado el modo de buscar furni satisfactoriamente"));
                break;
        }
    }

    @Override
    public String getPermission() {
        return "searchfurni_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Activa este comando y dale doble click a un furni que estés buscando y te llevará a la ubicación exacta";
    }
}
