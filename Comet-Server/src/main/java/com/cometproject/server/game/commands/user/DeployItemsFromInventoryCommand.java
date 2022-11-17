package com.cometproject.server.game.commands.user;

import com.cometproject.api.game.players.data.components.PlayerInventory;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.utilities.Position;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DeployItemsFromInventoryCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] message) {
        //check if the front tile is available
        /*final Position position = client.getPlayer().getEntity().getPosition();
        final Position squareInFront = position.squareInFront(client.getPlayer().getEntity().getBodyRotation());
        if (squareInFront == null) {
            sendWhisper("Não pode usar o comando à frente deste quadrado!", client);
            return;
        }

        //check if have any item on his inventory
        PlayerInventory inventory = client.getPlayer().getInventory();
        if (inventory.getTotalSize() == 0) {
            sendWhisper("O seu inventário está vazio!", client);
            return;
        }

        List<PlayerItem> floorItems = new ArrayList<>();

        inventory.getInventoryItems().forEach((id, item) -> {
            if (item.getDefinition().getType().equals("s"))
                floorItems.add(item);
        });

        if (!floorItems.isEmpty()) {
            floorItems.stream().limit(75).forEach(item -> {
                client.getPlayer().getEntity().getRoom().getBuilderComponent().placeFloorItem(client, item, squareInFront.getX(), squareInFront.getY(), 0);
            });
        }*/
    }

    @Override
    public String getPermission() {
        return "about_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.about.description", "Revisa la información del servidor.");
    }
}