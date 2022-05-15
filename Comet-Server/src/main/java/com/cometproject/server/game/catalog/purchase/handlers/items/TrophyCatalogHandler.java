package com.cometproject.server.game.catalog.purchase.handlers.items;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import org.joda.time.DateTime;

public class TrophyCatalogHandler extends BasicItemCatalogHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        String extraData = client.getPlayer().getData().getUsername() + Character.toChars(9)[0] + DateTime.now().getDayOfMonth() + "/" + DateTime.now().getMonthOfYear() + "/" + DateTime.now().getYear() + Character.toChars(9)[0] + data;
        super.execute(item, client, amount, page, giftData, definition, bundledItem, data, extraData);
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
