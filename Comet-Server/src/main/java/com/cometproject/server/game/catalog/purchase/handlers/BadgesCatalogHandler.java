package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.composers.catalog.BoughtItemMessageComposer;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.PurchaseHandler;
import com.cometproject.server.game.catalog.types.CatalogPage;
import com.cometproject.server.network.sessions.Session;

public class BadgesCatalogHandler extends PurchaseHandler implements IPurchaseHandler {

    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        client.getPlayer().getInventory().addBadge(item.getBadgeId(), true);

        client.getPlayer().getData().decreaseCredits(item.getCostCredits());
        client.getPlayer().getData().decreaseActivityPoints(item.getCostActivityPoints());
        client.getPlayer().getData().decreaseSeasonalPoints(item.getCostSeasonal());
        client.getPlayer().getData().decreaseVipPoints(item.getCostDiamonds());
        client.getPlayer().getData().save();
        client.getPlayer().sendBalance();

        client.send(new BoughtItemMessageComposer(BoughtItemMessageComposer.PurchaseType.BADGE));
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        if (item.hasBadge() && client.getPlayer().getInventory().hasBadge(item.getBadgeId())) {
            return false;
        }

        return true;
    }
}
