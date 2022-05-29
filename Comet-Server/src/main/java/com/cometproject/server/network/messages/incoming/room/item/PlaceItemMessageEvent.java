package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.user.building.FillType;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.types.components.BuildingComponent;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PlaceItemMessageEvent implements Event {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    @Override
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1, 1));
            return;
        }

        if (client.getPlayer() == null || client.getPlayer().getEntity() == null) {
            return;
        }

        if (!client.getPlayer().getEntity().getFillType().equals(FillType.NONE)
                && !client.getPlayer().getEntity().getRoom().getBuilderComponent().isBuilder(client.getPlayer().getEntity())) {
            return;
        }

        final String data = msg.readString();
        if (client.getPlayer().getInventory() == null) return;
        if (data.isEmpty()) return;

        final String[] parts = data.split(" ");
        final int id = Integer.parseInt(parts[0].replace("-", ""));
        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            final Map<String, String> notificationParams = Maps.newHashMap();
            notificationParams.put("message", "${room.error.cant_set_not_owner}");
            client.send(new NotificationMessageComposer("furni_placement_error", notificationParams));
            return;
        }

        BuildingComponent buildingComponent = client.getPlayer().getEntity().getRoom().getBuilderComponent();
        try {
            if (parts.length > 1 && parts[1].startsWith(":")) {
                buildingComponent.placeWallItem(client, parts, id);
            } else {
                final int x = Integer.parseInt(parts[1]), y = Integer.parseInt(parts[2]), rot = Integer.parseInt(parts[3]);
                final Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);
                if (itemId == null) {
                    return;
                }

                final PlayerItem item = client.getPlayer().getInventory().getItem(itemId);
                if (item == null) {
                    return;
                }

                switch (client.getPlayer().getEntity().getFillType()) {
                    case NONE: {
                        buildingComponent.placeFloorItem(client, item, x, y, rot);
                        break;
                    }

                    case FILL_STACK: {
                        buildingComponent.fillStack(client, x, y, rot, item);
                        break;
                    }

                    case FILL_ALL_BLOCKS: {
                        buildingComponent.fillArea(client, x, y, rot, item);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            client.getLogger().error("Error while placing item", e);
        }
    }

}
