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
            case "on":
                client.getPlayer().setIsSearchFurni(true);
                client.getPlayer().getSession().send(new NotificationMessageComposer("", "Você ativou com sucesso o modo de busca de mobis"));
                break;

            case "off":
                client.getPlayer().setIsSearchFurni(false);
                client.getPlayer().getSession().send(new NotificationMessageComposer("", "Você desativou com sucesso o modo de pesquisa de mobis"));
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
        return "Clique duas vezes em um mobi que você está procurando e ele o levará ao local exato";
    }
}
