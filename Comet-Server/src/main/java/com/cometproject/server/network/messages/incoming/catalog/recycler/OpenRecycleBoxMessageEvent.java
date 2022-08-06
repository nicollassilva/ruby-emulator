package com.cometproject.server.network.messages.incoming.catalog.recycler;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.GiftFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.List;
import java.util.Map;

public class OpenRecycleBoxMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        Room room = client.getPlayer().getEntity().getRoom();

        if(room == null)
            return;

        if(room.getData().getOwnerId() == client.getPlayer().getId() || client.getPlayer().getPermissions().getRank().roomFullControl()) {
            PlayerItem item = client.getPlayer().getInventory().getItem(msg.readInt());

            if(item == null)
                return;

            if(item instanceof GiftFloorItem) {
                if(item.getDefinition().getItemName().contains("present_wrap")) {
                }
            } else {
                if(item.getExtraData().length() == 0) {
                    client.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "Recycler box empty", 1));
                } else {
                    PlayerItem reward = ItemManager.getInstance().handleRecycle(client.getPlayer(), String.valueOf(item));

                    if(reward  != null) {
                        client.getPlayer().getInventory().addItem(reward);
                        client.getPlayer().getSession().send(new UnseenItemsMessageComposer((Map<Integer, List<Integer>>) reward));
                        client.getPlayer().getSession().send(new UpdateInventoryMessageComposer());
                    }

                    client.getPlayer().getEntity().getRoom().getItems().removeItem((RoomItemFloor) item, client, false, true, true);
                    room.getEntities().broadcastMessage(new RemoveFloorItemMessageComposer(item.getVirtualId(), client.getPlayer().getEntity().getRoom().getData().getOwnerId()));
                }
            }
        }
    }
}
