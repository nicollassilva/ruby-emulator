package com.cometproject.server.game.commands.user;

import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonPyramid;
import com.cometproject.server.network.sessions.Session;

public class PyramidCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        for (final RoomItemFloor floorItem : client.getPlayer().getEntity().getRoom().getItems().getFloorItems().values()) {
            if(floorItem instanceof WiredAddonPyramid || floorItem.getDefinition().getItemName().equals("wf_pyramid")) {
                if (!"0".equals(floorItem.getItemData().getData())) {
                    floorItem.getItemData().setData("0");
                } else {
                    floorItem.getItemData().setData("1");
                }

                floorItem.sendUpdate();
                floorItem.saveData();
            }
        }

    }

    @Override
    public String getPermission() {
        return "pyramid_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Sube o esconde las pirámides de tu sala";
    }
}
