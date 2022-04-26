package com.cometproject.server.game.commands.user;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionExecuteStacks;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionShowMessage;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PickAllWiredCommand extends ChatCommand {
    private String logDesc = "";

    @Override
    public void execute(Session client, String[] message) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || !room.getData().getOwner().equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.getOrDefault("command.need.rights", "You have no rights to use this command in this room."), client);
            return;
        }

        for (final RoomItem item : room.getItems().getFloorItems().values()) {
            if (item instanceof WiredFloorItem && item.getItemData().getOwnerId() == client.getPlayer().getId()) {
                room.getItems().removeItem((RoomItemFloor) item, client,true);
            }
        }

        room.getItems().getFloorItems().values().clear();

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "El staff %s ha hecho pickallwireds en la sala '%b', cuyo dueño es %c"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%b", room.getData().getName())
                .replace("%c", room.getData().getOwner());
    }

    @Override
    public String getPermission() {
        return "pickallwireds_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.pickallwireds.description", "Recoge todos los wireds de tu sala con este comando, sin necesidad de recoger todos los furnis de tu sala.");
    }

    @Override
    public String getLoggableDescription() {
        return this.logDesc;
    }

    @Override
    public boolean isLoggable() {
        return true;
    }
}
