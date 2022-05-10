package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.wall.PostItWallItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;
import java.util.List;


public class PickAllCommand extends ChatCommand {
    private String logDesc = "";

    @Override
    public void execute(Session client, String[] message) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || room.getData().getOwnerId() != client.getPlayer().getId()) {
            sendNotif(Locale.getOrDefault("command.need.rights", "You have no rights to use this command in this room."), client);
            return;
        }

        final List<RoomItem> itemsToRemove = new ArrayList<>();

        itemsToRemove.addAll(room.getItems().getFloorItems().values());
        itemsToRemove.addAll(room.getItems().getWallItems().values());

        for (final RoomItem item : itemsToRemove) {
            if(item instanceof PostItWallItem) continue;

            final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(item.getItemData().getOwnerId());

            item.onPickup();

            if (item instanceof RoomItemFloor && item.getItemData().getOwnerId() == client.getPlayer().getId()) {
                room.getItems().removeItem((RoomItemFloor) item, client);
            } else if (item instanceof RoomItemWall && item.getItemData().getOwnerId() == client.getPlayer().getId()) {
                room.getItems().removeItem((RoomItemWall) item, client, true);
            }

            if(item instanceof RoomItemFloor && item.getItemData().getOwnerId() != client.getPlayer().getId()) {
                room.getItems().removeItem((RoomItemFloor) item, session);
            } else if (item instanceof RoomItemWall && item.getItemData().getOwnerId() != client.getPlayer().getId()) {
                room.getItems().removeItem((RoomItemWall) item, session, true);
            }
        }

        itemsToRemove.clear();

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "El staff %s ha hecho pickall en la sala '%b', cuyo dueño es %c"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%b", room.getData().getName())
                .replace("%c", room.getData().getOwner());
    }

    @Override
    public String getPermission() {
        return "pickall_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.pickall.description", "Recoge todos los furnis de tu sala.");
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
