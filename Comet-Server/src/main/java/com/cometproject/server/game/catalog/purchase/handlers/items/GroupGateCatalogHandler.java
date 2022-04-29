package com.cometproject.server.game.catalog.purchase.handlers.items;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import org.apache.commons.lang3.StringUtils;

public class GroupGateCatalogHandler extends BasicItemCatalogHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        if (data.isEmpty() || !StringUtils.isNumeric(data)) return;

        if (!client.getPlayer().getGroups().contains(Integer.valueOf(data))) {
            return;
        }

        super.execute(item, client, amount, page, giftData, definition, bundledItem, data, data + ";0");
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
