package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.catalog.types.bundles.RoomBundleItem;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.composers.catalog.BoughtItemMessageComposer;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.PurchaseHandler;
import com.cometproject.server.game.catalog.types.CatalogPage;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.bundles.RoomBundleManager;
import com.cometproject.server.game.rooms.bundles.types.RoomBundle;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.settings.EnforceRoomCategoryMessageComposer;
import com.cometproject.server.storage.StorageManager;
import com.cometproject.server.storage.queries.items.ItemDao;
import com.cometproject.server.storage.queries.rooms.RoomItemDao;
import com.cometproject.server.storage.queue.items.containers.PlaceWallItemContainer;

public class RoomBundleCatalogHandler extends PurchaseHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        purchaseBundle(page, client);
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }

    private void purchaseBundle(ICatalogPage page, ISession client) {
        final RoomBundle roomBundle = RoomBundleManager.getInstance().getBundle(page.getExtraData());

        try {
            int roomId = RoomManager.getInstance().createRoom(roomBundle.getConfig().getRoomName().replace("%username%", client.getPlayer().getData().getUsername()), "", roomBundle.getRoomModelData(), 0, 20, 0, (int)Comet.getTime(), client, roomBundle.getConfig().getThicknessWall(), roomBundle.getConfig().getThicknessFloor(), roomBundle.getConfig().getDecorations(), roomBundle.getConfig().isHideWalls());

            for (final RoomBundleItem roomBundleItem : roomBundle.getRoomBundleData()) {
                long newItemId = ItemDao.createItem(client.getPlayer().getId(), roomBundleItem.getItemId(), roomBundleItem.getExtraData());

                if (roomBundleItem.getWallPosition() == null) {
                    RoomItemDao.placeFloorItem(roomId, roomBundleItem.getX(), roomBundleItem.getY(), roomBundleItem.getZ(), roomBundleItem.getRotation(), roomBundleItem.getExtraData(), newItemId);
                } else {

                    PlaceWallItemContainer container = new PlaceWallItemContainer(roomId, roomBundleItem.getWallPosition(), roomBundleItem.getExtraData(), newItemId);
                    StorageManager.getInstance().getQueues().getPlaceWallItemQueue().saveItem(container);
                }
            }

            client.getPlayer().getData().decreaseCredits(roomBundle.getCostCredits());
            client.getPlayer().getData().decreaseActivityPoints(roomBundle.getCostActivityPoints());
            client.getPlayer().getData().decreaseSeasonalPoints(roomBundle.getCostSeasonal());
            client.getPlayer().getData().decreaseVipPoints(roomBundle.getCostVip());
            client.getPlayer().getData().save();
            client.getPlayer().sendBalance();

            client.send(new RoomForwardMessageComposer(roomId));
            client.send(new EnforceRoomCategoryMessageComposer());
            client.send(new BoughtItemMessageComposer(BoughtItemMessageComposer.PurchaseType.BADGE));
            client.getPlayer().setLastRoomCreated((int) Comet.getTime());

        } catch (Exception e) {
            client.send(new MotdNotificationMessageComposer("Invalid room bundle data, please contact an administrator."));
            client.send(new BoughtItemMessageComposer(BoughtItemMessageComposer.PurchaseType.BADGE));
        }
    }
}
