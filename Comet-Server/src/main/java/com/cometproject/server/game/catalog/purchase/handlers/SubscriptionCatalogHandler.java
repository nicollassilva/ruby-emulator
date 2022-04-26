package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.catalog.types.subscriptions.IClubOffer;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.PurchaseHandler;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.PurchaseErrorMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;

public class SubscriptionCatalogHandler extends PurchaseHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        IClubOffer clubItem = CatalogManager.getInstance().clubOfferItems.get(itemId);

        if (clubItem == null) {
            client.send(new PurchaseErrorMessageComposer(0));
            return;
        }

        int totalDays = 0;
        int totalCredits = 0;
        int totalDiamonds = 0;

        for (int i = 0; i < amount; i++) {
            totalDays += clubItem.getDays();
            totalCredits += clubItem.getCredits();
            totalDiamonds += clubItem.getPoints();
        }

        if (totalDays > 0) {
            if (client.getPlayer().getData().getVipPoints() < totalDiamonds) {
                client.send(new NotificationMessageComposer("generic", "Você não tem diamantes suficientes para essa compra."));
                return;
            }

            if (client.getPlayer().getData().getCredits() < totalCredits) {
                client.send(new NotificationMessageComposer("generic", "Você não tem créditos suficientes para essa compra."));
                return;
            }

            client.getPlayer().getData().decreaseCredits(totalCredits);
            client.getPlayer().getData().decreaseVipPoints(totalDiamonds);
            client.getPlayer().getData().setTag("VIP");
            client.getPlayer().getData().setVip(true);
            client.getPlayer().getData().save();

            client.getPlayer().getSubscription().add(totalDays);
            client.send(client.getPlayer().getSubscription().confirm());

            client.getPlayer().sendBalance();

            client.send(new NotificationMessageComposer("VIP", Locale.getOrDefault("message.vip.buy", "Felicidades ya eres parte del club VIP de " + CometSettings.hotelName)));
            client.send(new UpdateInventoryMessageComposer());
        }
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
