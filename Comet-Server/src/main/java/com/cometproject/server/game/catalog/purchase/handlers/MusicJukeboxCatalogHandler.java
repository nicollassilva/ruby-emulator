package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.game.furniture.types.IMusicData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.PurchaseHandler;
import com.cometproject.server.game.catalog.purchase.handlers.items.BasicItemCatalogHandler;
import com.cometproject.server.game.items.ItemManager;

import java.util.Calendar;

public class MusicJukeboxCatalogHandler extends BasicItemCatalogHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
        final String songName = item.getPresetData();

        if(songName != null && !songName.isEmpty()) {
            IMusicData musicData = ItemManager.getInstance().getMusicDataByName(songName);

            if (musicData != null) {
                data = String.format("%s\n%s\n%s\n%s\n%s\n%s",
                        client.getPlayer().getData().getUsername(),
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                        musicData.getLengthSeconds(),
                        musicData.getTitle());
            }
        }
        super.execute(item, client, amount, page, giftData, definition, bundledItem, data, data);
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
