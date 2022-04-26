package com.cometproject.server.game.catalog.purchase.handlers.items;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.composers.catalog.BoughtItemMessageComposer;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;

public class NameColourCatalogHandler extends BasicItemCatalogHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
            if (item.getPresetData().equals("name_colour")) {
                client.getPlayer().getData().setNameColour(item.getBadgeId());
            } else {
                if (item.hasBadge() && !client.getPlayer().getInventory().hasBadge(item.getBadgeId())) {
                    client.getPlayer().getInventory().addBadge(item.getBadgeId(), true);
                }
            }

            client.send(new BoughtItemMessageComposer(BoughtItemMessageComposer.PurchaseType.BADGE));
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
