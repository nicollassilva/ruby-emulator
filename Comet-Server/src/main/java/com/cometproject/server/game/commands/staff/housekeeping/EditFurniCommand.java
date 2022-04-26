package com.cometproject.server.game.commands.staff.housekeeping;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class EditFurniCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        client.getPlayer().isFurnitureEditing(! client.getPlayer().getIsFurnitureEditing());

        if(client.getPlayer().getIsFurnitureEditing()) {
            sendNotif(Locale.getOrDefault("command.editfurni.enabled", "Clique 2x em qualquer mobi para abrir sua página no painel."), client);
        } else {
            sendNotif(Locale.getOrDefault("command.editfurni.disabled", "O modo editor de mobis foi desativado."), client);
        }
    }

    @Override
    public String getPermission() {
        return "editfurni_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.editfurni.description");
    }
}
