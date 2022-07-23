package com.cometproject.server.game.commands.gimmicks;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.components.ItemsComponent;
import com.cometproject.server.network.sessions.Session;

public class CountFurnisCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        //Verify rights
        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            sendWhisper("Você não tem permissão para usar este comando aqui!", client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        final ItemsComponent items = room.getItems();

        //Count items
        final int countWallItems = items.getWallItems().size();
        final int countFloorItems = items.getFloorItems().size();

        sendWhisper("Tem no total " + (countFloorItems + countWallItems) + " mobis no quarto! (Items de parede: " + countWallItems + ", Items de chão: " + countFloorItems + ")", client);
    }

    @Override
    public String getPermission() {
        return "count_furnis_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.count_furnis.description", "Diz quantos mobis tem no quarto.");
    }
}
