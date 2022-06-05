package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerStateChanged;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class UseRandomStateItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        try {
            final int itemId = msg.readInt();
            final int state = msg.readInt();

            if(client.getPlayer().getEntity() == null) return;

            final Room room = client.getPlayer().getEntity().getRoom();

            if(room == null) return;

            final RoomItemFloor item = room.getItems().getFloorItem(itemId);

            if(item == null) return;

            if(!client.getPlayer().getEntity().hasRights() || !client.getPlayer().getPermissions().getRank().roomFullControl()) return;

            if(item.onInteract(client.getPlayer().getEntity(), state, false)) {
                WiredTriggerStateChanged.executeTriggers(client.getPlayer().getEntity(), item);
            }
        } catch (Exception ignored) {

        }
    }
}
