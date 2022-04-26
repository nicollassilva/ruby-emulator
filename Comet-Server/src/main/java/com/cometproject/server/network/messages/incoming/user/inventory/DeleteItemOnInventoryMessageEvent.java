package com.cometproject.server.network.messages.incoming.user.inventory;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.items.ItemDao;

public class DeleteItemOnInventoryMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int itemVirtualId = msg.readInt();

        Long itemId = ItemManager.getInstance().getItemIdByVirtualId(itemVirtualId);

        if(itemId == null) return;

        PlayerItem item = client.getPlayer().getInventory().getItem(itemId);

        if(item == null) return;

        if(!item.getDefinition().canDelete()) {
            client.send(new NotificationMessageComposer("generic", Locale.getOrDefault("item_not_deleteable", "Você não pode deletar esse item.")));
            return;
        }

        ItemDao.deleteItemsFromInventory(item.getDefinition().getId(), client.getPlayer().getData().getId());

        client.getPlayer().getInventory().removeItems(item.getDefinition().getId());
    }
}
