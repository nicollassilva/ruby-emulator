package com.cometproject.server.game.catalog.purchase;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.catalog.types.purchase.CatalogPurchase;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.game.furniture.types.IGiftData;
import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.api.game.rooms.objects.data.LimitedEditionItemData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.utilities.JsonUtil;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.types.CatalogBundledItem;
import com.cometproject.server.game.catalog.types.CatalogItem;
import com.cometproject.server.game.catalog.types.CatalogPage;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.items.types.ItemDefinition;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.catalog.CatalogDao;
import com.cometproject.server.storage.queries.items.ItemDao;
import com.cometproject.server.storage.queries.items.LimitedEditionDao;
import com.cometproject.server.storage.queries.items.TeleporterDao;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PurchaseHandler implements IPurchaseHandler {
    public void giftPurchase(ICatalogItem item, ISession client, int amount, boolean isTeleport, IGiftData giftData, String extraData, int playerIdToDeliver) {
        long[] teleportIds = null;

        if (isTeleport) {
            teleportIds = new long[amount];
        }

        List<CatalogPurchase> purchases = new ArrayList<>();
        giftData.setExtraData(extraData);
        FurnitureDefinition itemDefinition = ItemManager.getInstance().getBySpriteId(giftData.getSpriteId());
        purchases.add(new CatalogPurchase(playerIdToDeliver, itemDefinition == null ? CatalogManager.getInstance().getGiftBoxesOld().get(0) : itemDefinition.getId(), "GIFT::##" + JsonUtil.getInstance().toJson(giftData)));

        List<Long> newItems = ItemDao.createItems(purchases);
        for (long newItem : newItems) {
            if (item.getLimitedTotal() > 0) {
                LimitedEditionDao.save(new LimitedEditionItemData(newItem, item.getLimitedSells(), item.getLimitedTotal()));
            }
            if (isTeleport) {
                teleportIds[newItems.indexOf(newItem)] = newItem;
            }
        }
        if (isTeleport) {
            for (int i = 0; i < teleportIds.length; i++) {
                if (i % 2 != 0) {
                    TeleporterDao.savePair(teleportIds[i], teleportIds[i - 1]);
                }
            }
        }
            this.deliverGift(playerIdToDeliver, giftData, newItems, client.getPlayer().getData().getUsername());
            PlayerDao.putPurchasedGift(client.getPlayer().getId(), giftData.getItemId(), playerIdToDeliver);
    }

    public void itemPurchase(ICatalogItem item, ICatalogBundledItem bundledItem, ISession client, int amount, ICatalogPage page, boolean isTeleport, GiftData giftData, String extraData) {
        long[] teleportIds = null;

        if (isTeleport) {
            teleportIds = new long[amount];
        }

        final List<CatalogPurchase> purchases = new ArrayList<>();

        for (int purchaseCount = 0; purchaseCount < amount; purchaseCount++) {
            for (int itemCount = 0; itemCount != bundledItem.getAmount(); itemCount++) {
                purchases.add(new CatalogPurchase(client.getPlayer().getId(), bundledItem.getItemId(), extraData));
            }
        }

        final List<Long> newItems = ItemDao.createItems(purchases);
        final Set<PlayerItem> unseenItems = Sets.newHashSet();

        for (final long newItem : newItems) {
            if (item.getLimitedTotal() > 0) {
                LimitedEditionDao.save(new LimitedEditionItemData(newItem, item.getLimitedSells(), item.getLimitedTotal()));
                LimitedEditionDao.updateItemLimitedData(newItem, item.getLimitedSells(), item.getLimitedTotal(), client.getPlayer().getId());
            }

            unseenItems.add(client.getPlayer().getInventory().add(newItem, bundledItem.getItemId(), extraData, giftData, item.getLimitedTotal() > 0 ? new LimitedEditionItemData(bundledItem.getItemId(), item.getLimitedSells(), item.getLimitedTotal()) : null));

            if (isTeleport) {
                teleportIds[newItems.indexOf(newItem)] = newItem;
            }
        }

        if (isTeleport) {
            for (int i = 0; i < teleportIds.length; i++) {
                if (i % 2 != 0) {
                    TeleporterDao.savePair(teleportIds[i], teleportIds[i - 1]);
                }
            }
        }

        if (item.hasBadge()) {
            client.getPlayer().getInventory().addBadge(item.getBadgeId(), true);
        }

        client.send(new UnseenItemsMessageComposer(unseenItems));
        client.send(new UpdateInventoryMessageComposer());

        CatalogDao.saveRecentPurchase(client.getPlayer().getId(), item.getId(), amount, extraData);
        client.getPlayer().getRecentPurchases().add(item);
    }

    private void deliverGift(int playerId, IGiftData giftData, List<Long> newItems, String senderUsername) {
        Session client = NetworkManager.getInstance().getSessions().fromPlayer(playerId);

        if (client != null) {
            Set<PlayerItem> unseenItems = Sets.newHashSet();

            if (client.getPlayer() != null) {
                if (client.getPlayer().getInventory() != null) {
                    for (long newItem : newItems) {
                        unseenItems.add(client.getPlayer().getInventory().add(newItem, ItemManager.getInstance().getBySpriteId(giftData.getSpriteId()).getId(), "GIFT::##" + JsonUtil.getInstance().toJson(giftData), giftData, null));
                    }
                }

                if (client.getPlayer().getAchievements() != null) {
                    client.getPlayer().getAchievements().progressAchievement(AchievementType.GIFT_RECEIVER, 1);
                }
            }

            client.send(new UnseenItemsMessageComposer(unseenItems));
            client.send(new UpdateInventoryMessageComposer());
            client.send(new NotificationMessageComposer("gift_received", Locale.get("notification.gift_received").replace("%username%", senderUsername)));

        }
    }

    public boolean canPurchase(ICatalogItem item, Session client) {
        return true;
    }
}
