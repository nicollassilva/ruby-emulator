package com.cometproject.server.game.commands.user.room;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.wall.PostItWallItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveWallItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageComposer;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PickAllCommand extends ChatCommand {
    private String logDesc = "";

    @Override
    public void execute(Session client, String[] message) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || room.getData().getOwnerId() != client.getPlayer().getId()) {
            sendNotif(Locale.getOrDefault("command.need.rights", "You have no rights to use this command in this room."), client);
            return;
        }

        final List<RoomItem> itemsToRemove = new ArrayList<>(room.getItems().getFloorItems().size() + room.getItems().getWallItems().size());
        itemsToRemove.addAll(room.getItems().getFloorItems().values());
        itemsToRemove.addAll(room.getItems().getWallItems().values());

        final ArrayList<MessageComposer> toClient = new ArrayList<>();
        final ArrayList<MessageComposer> toRoom = new ArrayList<>();

        final Set<PlayerItem> unseenItems = new HashSet<>();
        for (final RoomItem item : itemsToRemove) {
            if(item instanceof PostItWallItem) continue;

            final Session session = NetworkManager.getInstance().getSessions().getByPlayerId(item.getItemData().getOwnerId());
            item.onPickup();
            if(item instanceof RoomItemFloor) {
                room.getItems().removeItem((RoomItemFloor) item, session, true, false);
                toRoom.add(new RemoveFloorItemMessageComposer(item.getVirtualId(), item.getItemData().getOwnerId()));
            } else if (item instanceof RoomItemWall) {
                room.getItems().removeItem((RoomItemWall) item, session, true, false);
                toRoom.add(new RemoveWallItemMessageComposer(item.getVirtualId(), item.getItemData().getOwnerId()));
            }

            if(item.getItemData().getOwnerId() == client.getPlayer().getId()){
                unseenItems.add(client.getPlayer().getInventory().getItem(item.getId()));
            }
        }
        toClient.add(new UpdateInventoryMessageComposer());
        toClient.add(new UnseenItemsMessageComposer(unseenItems));

        toClient.forEach(client::sendQueue);
        client.flush();

        toRoom.forEach(client.getPlayer().getEntity().getRoom().getEntities()::broadcastMessage);

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
    public boolean canDisable() {
        return true;
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
