package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.rooms.objects.IRoomItemData;
import com.cometproject.server.game.catalog.marketplace.MarketPlace;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceItemPostedComposer;
import com.cometproject.server.network.messages.outgoing.notification.PurchaseErrorMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class SellItemEvent extends MarketPlaceEvent {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int credits = msg.readInt();
        int unknown = msg.readInt();
        int itemId = msg.readInt();

        Long realItemId = ItemManager.getInstance().getItemIdByVirtualId(itemId);

        if(realItemId == null) {
            client.send(new PurchaseErrorMessageComposer(0));
            return;
        }

        PlayerItem item = client.getPlayer().getInventory().getItem(realItemId);

        if(item == null) {
            client.send(new PurchaseErrorMessageComposer(0));
            return;
        }

        if(!item.getDefinition().canMarket()) {
            client.send(new PurchaseErrorMessageComposer(0));
            return;
        }

        if(credits <= 0) {
            client.send(new PurchaseErrorMessageComposer(0));
            return;
        }

        if(MarketPlace.sellItem(client, item, credits)) {
            client.send(new MarketplaceItemPostedComposer(MarketplaceItemPostedComposer.POST_SUCCESS));
        } else {
            client.send(new MarketplaceItemPostedComposer(MarketplaceItemPostedComposer.FAILED_TECHNICAL_ERROR));
        }
    }
}
