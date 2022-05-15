package com.cometproject.server.game.commands.user.room;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class SeeHeightCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())
            return;

        final boolean viewingHeight = client.getPlayer().viewingHeight();

        client.getPlayer().setHeightView(!viewingHeight);

        if(!viewingHeight) {
            sendNotif(Locale.getOrDefault("command.see_height.activated", "Comando ativo, clique duas vezes no mobi que deseja ver a altura."), client);
            return;
        }

        sendNotif(Locale.getOrDefault("command.see_height.disabled", "Comando desativado."), client);
    }

    @Override
    public String getPermission() {
        return "see_height_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.see_height.description", "Vê a altura de um mobi clicando 2x no mesmo");
    }
}
