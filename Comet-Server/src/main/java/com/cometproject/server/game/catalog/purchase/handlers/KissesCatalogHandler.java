package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.game.players.data.IPlayerStatistics;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.composers.catalog.BoughtItemMessageComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.UserObjectMessageComposer;

public class KissesCatalogHandler implements IPurchaseHandler {
    public final int KISSES_LIMIT = 200;

    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        String[] extraData = item.getPresetData().split(":");

        if(extraData.length < 2) return;

        if(!extraData[0].equalsIgnoreCase("kisses")) return;

        final IPlayerStatistics playerStats = client.getPlayer().getStats();
        final int kissesAmount = Integer.parseInt(extraData[1]);

        if(playerStats == null) return;

        if(playerStats.getTotalKisses() + kissesAmount >= KISSES_LIMIT) {
            client.send(new NotificationMessageComposer(
                    "kisses",
                    Locale.getOrDefault("kisses.buy.limit.reached", "Você atingiu o número máximo de beijos permitidos.")
            ));

            return;
        }

        client.getPlayer().getInventory().addBadge(item.getBadgeId(), true);

        client.getPlayer().getStats().incrementTotalKisses(kissesAmount);
        client.getPlayer().getStats().save();

        client.getPlayer().getData().decreaseCredits(item.getCostCredits());
        client.getPlayer().getData().decreaseActivityPoints(item.getCostActivityPoints());
        client.getPlayer().getData().decreaseSeasonalPoints(item.getCostSeasonal());
        client.getPlayer().getData().decreaseVipPoints(item.getCostDiamonds());
        client.getPlayer().getData().save();

        client.getPlayer().sendBalance();

        client.send(new BoughtItemMessageComposer(BoughtItemMessageComposer.PurchaseType.BADGE));
        client.send(new UserObjectMessageComposer((Player) client.getPlayer()));

        client.send(new NotificationMessageComposer(
                "kisses",
                Locale.getOrDefault("kisses.buy.success", "Você comprou %amount% beijos.").replace("%amount%", kissesAmount + "")
        ));
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
