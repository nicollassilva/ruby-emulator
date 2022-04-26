package com.cometproject.server.game.catalog.purchase.handlers.items;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.catalog.purchase.PurchaseHandler;

public abstract class BasicItemCatalogHandler extends PurchaseHandler {

    public void execute(ICatalogItem item, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data, String extraData) {
        if (giftData != null) {
            super.giftPurchase(item, client, amount, definition.isTeleporter(), giftData, extraData, giftData.getDeliveryId());
        } else {
            super.itemPurchase(item, bundledItem, client, amount, page, definition.isTeleporter(), giftData, extraData);
        }
    }
}
