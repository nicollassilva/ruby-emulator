package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.StorageManager;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class ExchangeItemMessageEvent implements Event {
    @Override
    synchronized public void handle(Session client, MessageEvent msg) {
        final int virtualId = msg.readInt();
        final long itemId = ItemManager.getInstance().getItemIdByVirtualId(virtualId);

        StorageManager storage = StorageManager.getInstance();

        if (storage.idIsBlocked(itemId)) {
            return;
        }

        if (!storage.blockItemId(itemId)) {
            return;
        }

        if (client.getPlayer().getEntity() == null) {
            storage.unblockItemId(itemId);
            return;
        }
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            storage.unblockItemId(itemId);

            return;
        }

        final RoomItemFloor item = room.getItems().getFloorItem(itemId);

        if (item == null || item.getIsLocked()) {
            storage.unblockItemId(itemId);
            return;
        }
        if (item.getItemData().getOwnerId() != client.getPlayer().getId()) {
            storage.unblockItemId(itemId);
            return;
        }

        item.setIsLocked(true);

        int value;
        boolean isDiamond = false;
        boolean isRuby = false;

        if (!item.getDefinition().getItemName().startsWith("CF_") && !item.getDefinition().getItemName().startsWith("CFC_")) {
            storage.unblockItemId(itemId);
            return;
        }

        if (item.getDefinition().getItemName().contains("_diamond_")) {
            isDiamond = true;
            value = Integer.parseInt(item.getDefinition().getItemName().split("_diamond_")[1]);
        } else if (item.getDefinition().getItemName().contains("_ruby_")) {
            isRuby = true;
            value = Integer.parseInt(item.getDefinition().getItemName().split("_ruby_")[1]);
        } else {
            value = Integer.parseInt(item.getDefinition().getItemName().split("_")[1]);
        }

        room.getItems().removeItem(item, client, false, true, true);
        String exchangeValue;

        if (isDiamond) {
            client.getPlayer().getData().increaseVipPoints(value);
            exchangeValue = "Diamonds: " + client.getPlayer().getData().getVipPoints();
        } else if (isRuby) {
            client.getPlayer().getData().increaseSeasonalPoints(value);
            exchangeValue = "Rubys: " + client.getPlayer().getData().getSeasonalPoints();
        } else {
            client.getPlayer().getData().increaseCredits(value);
            exchangeValue = "Credits: " + client.getPlayer().getData().getCredits();
        }

        PlayerDao.saveExchangeLog(client.getPlayer().getData().getId(), itemId, item.getDefinition().getId(), exchangeValue);

        client.getPlayer().sendBalance();
        client.getPlayer().getData().save();
        storage.unblockItemId(itemId);

    }

}
