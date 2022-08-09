package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.CometServer;
import com.cometproject.server.composers.catalog.CatalogOfferMessageComposer;
import com.cometproject.server.composers.catalog.CatalogPublishMessageComposer;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.handlers.items.BasicItemCatalogHandler;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceConfigComposer;
import com.cometproject.server.storage.queries.catalog.CatalogDao;

public class LimitedCatalogHandler extends BasicItemCatalogHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        item.increaseLimitedSells(amount);
        CatalogDao.updateLimitSellsForItem(item.getId(), amount);
        client.send(new CatalogOfferMessageComposer(item));
        super.execute(item, client, amount, page, giftData, definition, bundledItem, data, "");

        if(item.getLimitedSells() >= item.getLimitedTotal()){ // sold out
            CatalogDao.soldOutItem(itemId, CometSettings.CATALOG_SOLD_OUT_LTD_PAGE_ID);
            CatalogManager.getInstance().loadGiftBoxes();
            CatalogManager.getInstance().loadItemsAndPages();
            CatalogManager.getInstance().loadClothingItems();
            client.send(new CatalogPublishMessageComposer(true));

            NetworkManager.getInstance().getSessions().broadcast(new MarketplaceConfigComposer());
            for (final ISession session : NetworkManager.getInstance().getSessions().getSessions().values()) {
                if(session.getPlayer().getId() != client.getPlayer().getId()) {
                    client.send(new CatalogPublishMessageComposer(false));
                }
            }
        }
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return item.getLimitedSells() < item.getLimitedTotal();
    }
}
