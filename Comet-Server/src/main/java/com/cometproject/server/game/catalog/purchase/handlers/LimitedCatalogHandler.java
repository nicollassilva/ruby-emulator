package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.composers.catalog.CatalogOfferMessageComposer;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.handlers.items.BasicItemCatalogHandler;
import com.cometproject.server.storage.queries.catalog.CatalogDao;

public class LimitedCatalogHandler extends BasicItemCatalogHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        item.increaseLimitedSells(amount);
        CatalogDao.updateLimitSellsForItem(item.getId(), amount);
        client.send(new CatalogOfferMessageComposer(item));
        super.execute(item, client, amount, page, giftData, definition, bundledItem, data, "");
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        if (item.getLimitedSells() < item.getLimitedTotal()) {
            return true;
        }

        return false;
    }
}
