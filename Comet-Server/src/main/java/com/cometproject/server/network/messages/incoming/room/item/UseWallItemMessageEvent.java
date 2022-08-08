package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.misc.OpenLinkMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.Map;


public class UseWallItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        int virtualId = msg.readInt();

        Long itemId = ItemManager.getInstance().getItemIdByVirtualId(virtualId);

        if (itemId == null) {
            return;
        }

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        RoomItemWall item = client.getPlayer().getEntity().getRoom().getItems().getWallItem(itemId);

        if (item == null) {
            return;
        }

        int requestData = msg.readInt();

        if (!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        if(client.getPlayer().getIsFurnitureEditing() && ! CometExternalSettings.housekeepingFurnitureEdition.isEmpty()) {
            client.send(new OpenLinkMessageComposer(CometExternalSettings.housekeepingFurnitureEdition.replace("{id}", item.getDefinition().getId() + "")));

            return;
        }

        if (client.getPlayer().getIsFurniturePickup()) {
            Map<Long, RoomItemWall> wallItems = client.getPlayer().getEntity().getRoom().getItems().getWallItems();
            int count = 0;

            for (final RoomItemWall wallItem : wallItems.values()) {
                if(wallItem == null) continue;

                if(wallItem.getItemData().getOwnerId() != client.getPlayer().getId()) continue;

                if(wallItem.getDefinition().getId() != item.getDefinition().getId()) continue;

                wallItem.onPickup();
                client.getPlayer().getEntity().getRoom().getItems().removeItem(wallItem, client, true, true);
                count++;
            }

            client.getPlayer().isFurniturePickup(false);
            client.send(new NotificationMessageComposer(
                    "generic",
                    Locale.getOrDefault("command.pickup.success", "Foram coletados %count% mobis.").replace("%count%", count + "")
            ));
            return;
        }

        item.onInteract(client.getPlayer().getEntity(), requestData, false);
    }
}
