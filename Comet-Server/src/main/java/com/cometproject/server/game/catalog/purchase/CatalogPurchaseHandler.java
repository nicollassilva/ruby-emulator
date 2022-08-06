package com.cometproject.server.game.catalog.purchase;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.config.Configuration;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.catalog.types.CatalogPageType;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.catalog.types.purchase.ICatalogPurchaseHandler;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.composers.catalog.BoughtItemMessageComposer;
import com.cometproject.server.composers.catalog.GiftUserNotFoundMessageComposer;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.purchase.handlers.*;
import com.cometproject.server.game.catalog.purchase.handlers.items.*;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.PurchaseErrorMessageComposer;
import com.cometproject.server.storage.queries.player.PlayerDao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogPurchaseHandler implements ICatalogPurchaseHandler {
    private final Map<String, IPurchaseHandler> handlers;
    private ExecutorService executorService;

    public CatalogPurchaseHandler() {
        this.handlers = new HashMap<>();

        this.handlers.put("badge_display", new BadgeDisplayCatalogHandler());
        this.handlers.put("badges", new BadgesCatalogHandler());
        this.handlers.put("kisses", new KissesCatalogHandler());
        this.handlers.put("rentable_bot", new BotsCatalogHandler());
        //this.handlers.put("emoticone", new EmoticoneCatalogHandler());
        this.handlers.put("group_forum", new GroupForumCatalogHandler());
        this.handlers.put("group_gate", new GroupGateCatalogHandler());
        this.handlers.put("group_item", new GroupItemCatalogHandler());
        this.handlers.put("limited", new LimitedCatalogHandler());
        this.handlers.put("pets", new PetsCatalogHandler());
        //this.handlers.put("pseudocolor", new PseudoColorCatalogHandler());
        this.handlers.put("roombundle", new RoomBundleCatalogHandler());
        this.handlers.put("teleport", new TeleporterCatalogHandler());
        this.handlers.put("teleport_door", new TeleporterCatalogHandler());
        this.handlers.put("teleport_pad", new TeleporterCatalogHandler());
        this.handlers.put("trophy", new TrophyCatalogHandler());
        this.handlers.put("musicjukebox", new MusicJukeboxCatalogHandler());
        this.handlers.put("roomeffect", new WallPaperCatalogHandler());
        this.handlers.put("subscription", new SubscriptionCatalogHandler());
        this.handlers.put("name_colour", new NameColourCatalogHandler());
        this.handlers.put("default", new DefaultCatalogHandler());
    }

    @Override
    public synchronized void purchaseItem(ISession client, int pageId, int itemId, String data, int amount, GiftData giftData) {
        if (CometSettings.CATALOG_ASYNC_PURCHASE_ALLOW) {
            if (this.executorService == null) {
                this.executorService = Executors.newFixedThreadPool(Integer.parseInt(Configuration.currentConfig().get("comet.system.catalogPurchaseThreads")));
            }

            this.executorService.submit(() -> this.handle(client, pageId, itemId, data, amount, giftData));
        } else {
            this.handle(client, pageId, itemId, data, amount, giftData);
        }
    }

    @Override
    public void handle(ISession client, int pageId, int itemId, String data, int amount, GiftData giftData) {
        final int playerIdToDeliver = giftData == null ? -1 : PlayerDao.getIdByUsername(giftData.getReceiver());

        if (this.canHandle(client, amount, giftData, playerIdToDeliver)) {
            ICatalogPage page = CatalogManager.getInstance().getPage(pageId);
            ICatalogItem item;

            if(page == null) {
                page = CatalogManager.getInstance().getCatalogPageByCatalogItemId(itemId);
            }

            if (page != null) {
                if (page.isVipOnly() && client.getPlayer().getData().getRank() != CometSettings.vipRank && client.getPlayer().getData().getRank() < CometSettings.rankCanSeeVipContent) return;

                if (page.getMinRank() <= client.getPlayer().getData().getRank() || page.getItems().containsKey(itemId)) {
                    item = page.getItems().get(itemId);

                    if (item == null && page.getType() == CatalogPageType.RECENT_PURCHASES) {
                        item = CatalogManager.getInstance().getCatalogItem(itemId);

                        if (item == null) {
                            client.send(new PurchaseErrorMessageComposer(2));
                            return;
                        }

                        final ICatalogPage realCatalogPage = CatalogManager.getInstance().getPage(item.getPageId());

                        if (realCatalogPage == null) {
                            client.send(new PurchaseErrorMessageComposer(2));
                            return;
                        }

                        if (realCatalogPage.isVipOnly() && client.getPlayer().getData().getRank() != CometSettings.vipRank && client.getPlayer().getData().getRank() < CometSettings.rankCanSeeVipContent) return;

                        if (realCatalogPage.getMinRank() > client.getPlayer().getData().getRank() || !realCatalogPage.getItems().containsKey(itemId)) return;
                    }

                    if (item == null || page.getTemplate().equals("single_bundle") || page.getType() == CatalogPageType.BUNDLE) {
                        this.purchaseHandlerByPage(page, itemId, client, amount, giftData, data);
                        return;
                    }

                    if (this.itemHasCustomPurchase(item, page, client, amount)) {
                        return;
                    }

                    if (giftData != null && !this.canGift(client, item)) {
                        return;
                    }

                    if (amount > 1 && !item.allowOffer()) {
                        return;
                    }

                    if (client.getPlayer().getData().getRank() < page.getMinRank()) {
                        return;
                    }

                    final int totalCostCredits = item.isZeroCredits() ? 0 : CatalogManager.getInstance().calculateDiscountedPrice(item.getCostCredits(), amount, item);
                    final int totalCostVipPoints = item.isZeroDiamonds() ? 0 : CatalogManager.getInstance().calculateDiscountedPrice(item.getCostDiamonds(), amount, item);
                    final int totalCostSeasonalPoints = item.isZeroSeasonal() ? 0 : CatalogManager.getInstance().calculateDiscountedPrice(item.getCostSeasonal(), amount, item);
                    final int totalCostActivityPoints = item.isZeroActivityPoints() ? 0 : CatalogManager.getInstance().calculateDiscountedPrice(item.getCostActivityPoints(), amount, item);

                    if (((client.getPlayer().getData().getCredits() >= totalCostCredits && client.getPlayer().getData().getActivityPoints() >= totalCostActivityPoints)) && client.getPlayer().getData().getVipPoints() >= totalCostVipPoints && client.getPlayer().getData().getSeasonalPoints() >= totalCostSeasonalPoints) {
                        final FurnitureDefinition definition = ItemManager.getInstance().getDefinition(Integer.parseInt(item.getItemId()));

                        if (definition == null) {
                            return;
                        }

                        String handleName = definition.getInteraction();
                        boolean isItem = true;

                        if (item.getLimitedTotal() != 0) {
                            handleName = "limited";
                        } else if (item.getDisplayName().startsWith("a0 pet")) {
                            handleName = "pets";
                            isItem = false;
                        } else if (item.getPresetData().equals("name_colour")) {
                            handleName = "name_colour";
                            isItem = false;
                        } else if (definition.getType().equals("r")) {
                            handleName = "rentable_bot";
                        }
                        if (giftData != null) {
                            giftData.setDeliveryId(playerIdToDeliver);
                        } else if (definition.isRoomDecor()) {
                            handleName = "roomeffect";
                            //isItem = false;
                        } else if (definition.getInteraction().equals("trophy")) {
                            handleName = "trophy";
                        } else if (definition.getInteraction().equals("badge_display")) {
                            handleName = "badge_display";
                        }

                        IPurchaseHandler handler = this.handlers.get(handleName);

                        if (handler == null) {
                            handler = this.handlers.get("default");
                        }

                        if (handler.canPurchase(item, client)) {
                            if (!isItem) {
                                handler.purchase(item, itemId, client, amount, page, giftData, definition, null, data);
                            } else {
                                for (final ICatalogBundledItem bundledItem : item.getItems()) {
                                    handler.purchase(item, itemId, client, amount, page, giftData, definition, bundledItem, data);
                                }
                            }

                            client.send(new BoughtItemMessageComposer(item, definition));

                            client.getPlayer().getData().decreaseCredits(totalCostCredits);
                            client.getPlayer().getData().decreaseActivityPoints(totalCostActivityPoints);
                            client.getPlayer().getData().decreaseVipPoints(totalCostVipPoints);
                            client.getPlayer().getData().decreaseSeasonalPoints(totalCostSeasonalPoints);
                            client.getPlayer().getData().save();
                            client.getPlayer().sendBalance();
                        }
                    } else {
                        client.send(new PurchaseErrorMessageComposer(2));
                    }
                }
            }
        }
    }

    public void purchaseHandlerByPage(final ICatalogPage page, final int itemId, final ISession client, final int amount, final GiftData giftData, final String data) {
        String handleName = "unknownHandle";

        if (page.getTemplate().equals("vip_buy")) {
            handleName = "subscription";
        }

        if (page.getTemplate().equals("single_bundle") || page.getType() == CatalogPageType.BUNDLE) {
            handleName = "roombundle";
        }

        IPurchaseHandler handler = this.handlers.get(handleName);

        if (handler == null) return;

        handler.purchase(null, itemId, client, amount, page, giftData, null, null, data);
    }

    public boolean itemHasCustomPurchase(ICatalogItem item, ICatalogPage page, ISession client, int amount) {
        if (item.hasBadge() && item.getItemId().equals("-1")) {

            if (item.getPresetData().contains("name_colour")) {
                return true;
            }

            if (item.getPresetData().contains("kisses")) {
                this.purchaseKisses(page, item, client, amount);
                return true;
            }

            this.purchaseBadge(page, item, client, amount);
            return true;
        }

        return false;
    }

    public void purchaseKisses(ICatalogPage page, ICatalogItem item, ISession client, int amount) {
        final IPurchaseHandler handler = this.handlers.get("kisses");

        if (handler == null) return;

        if (handler.canPurchase(item, client)) {
            handler.purchase(item, item.getId(), client, amount, page, null, null, null, "");
        } else {
            client.send(new PurchaseErrorMessageComposer(1));
        }
    }

    public void purchaseBadge(ICatalogPage page, ICatalogItem item, ISession client, int amount) {
        final IPurchaseHandler handler = this.handlers.get("badges");

        if (handler == null) return;

        if (handler.canPurchase(item, client)) {
            handler.purchase(item, item.getId(), client, amount, page, null, null, null, "");
        } else {
            client.send(new PurchaseErrorMessageComposer(1));
        }
    }

    @Override
    public boolean canHandle(ISession client, int amount, GiftData giftData, int playerIdToDeliver) {
        if (client == null || client.getPlayer() == null) return false;

        if (amount > 100) {
            client.send(new AlertMessageComposer(Locale.get("catalog.error.toomany")));
            return false;
        }

        if (giftData == null) {
            return true;
        }

        if (playerIdToDeliver == 0) {
            client.send(new GiftUserNotFoundMessageComposer());
            return false;
        } else {
            if (client.getPlayer().getMessenger().getFriendById(playerIdToDeliver) == null && !client.getPlayer().getPermissions().getRank().modTool()) {
                client.send(new GiftUserNotFoundMessageComposer());
                return false;
            }

            client.getPlayer().getAchievements().progressAchievement(AchievementType.GIFT_GIVER, 1);
        }

        return true;
    }

    @Override
    public boolean canGift(ISession client, ICatalogItem item) {
        final FurnitureDefinition itemDefinition = ItemManager.getInstance().getDefinition(item.getItems().get(0).getItemId());

        if (itemDefinition == null) {
            return false;
        }

        if (!itemDefinition.canGift()) {
            return false;
        }

        if (client.getPlayer().getLastGift() != 0 && !client.getPlayer().getPermissions().getRank().floodBypass()) {
            if (((int) Comet.getTime() - client.getPlayer().getLastGift()) < CometSettings.PLAYER_GIFT_COOLDOWN) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("catalog.error.gifttoofast")));
                client.send(new BoughtItemMessageComposer(BoughtItemMessageComposer.PurchaseType.BADGE));
                return false;
            }
        }

        client.getPlayer().setLastGift((int) Comet.getTime());
        return true;
    }
}

