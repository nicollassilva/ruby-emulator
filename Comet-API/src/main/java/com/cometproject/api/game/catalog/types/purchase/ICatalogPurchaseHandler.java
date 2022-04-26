package com.cometproject.api.game.catalog.types.purchase;

import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.catalog.types.bundles.IRoomBundle;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.game.furniture.types.IGiftData;
import com.cometproject.api.networking.sessions.ISession;

import java.util.List;

public interface ICatalogPurchaseHandler {
    void purchaseItem(ISession client, int pageId, int itemId, String data, int amount, GiftData giftData);

    void handle(ISession client, int pageId, int itemId, String data, int amount, GiftData giftData);

    boolean canHandle(ISession client, int amount, GiftData giftData, int playerIdToDeliver);

    boolean canGift(ISession client, ICatalogItem item);
}
