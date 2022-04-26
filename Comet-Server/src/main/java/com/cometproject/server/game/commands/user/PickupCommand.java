package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

public class PickupCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final boolean playerIsPickup = client.getPlayer().getIsFurniturePickup();

        client.getPlayer().isFurniturePickup(!playerIsPickup);

        if(!playerIsPickup) {
            sendNotif(Locale.getOrDefault("command.pickup.activated", "Comando ativo, clique duas vezes no mobi que deseja recolher."), client);
            return;
        }

        sendNotif(Locale.getOrDefault("command.pickup.disabled", "Comando desativado."), client);
    }

    @Override
    public String getPermission() {
        return "pickup_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.pickup.description", "Recolhe todos os mobis iguais ao que você escolher.");
    }
}
