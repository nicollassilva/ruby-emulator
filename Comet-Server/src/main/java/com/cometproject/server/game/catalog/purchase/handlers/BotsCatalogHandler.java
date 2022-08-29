package com.cometproject.server.game.catalog.purchase.handlers;

import com.cometproject.api.game.bots.BotMode;
import com.cometproject.api.game.bots.BotType;
import com.cometproject.api.game.bots.IBotData;
import com.cometproject.api.game.catalog.types.ICatalogBundledItem;
import com.cometproject.api.game.catalog.types.ICatalogItem;
import com.cometproject.api.game.catalog.types.ICatalogPage;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.GiftData;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.catalog.purchase.IPurchaseHandler;
import com.cometproject.server.game.catalog.purchase.PurchaseHandler;
import com.cometproject.server.game.rooms.objects.entities.types.data.PlayerBotData;
import com.cometproject.server.network.messages.incoming.catalog.data.UnseenItemsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.BotInventoryMessageComposer;
import com.cometproject.server.storage.queries.bots.PlayerBotDao;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;

public class BotsCatalogHandler extends PurchaseHandler implements IPurchaseHandler {
    @Override
    public void purchase(ICatalogItem item, int itemId, ISession client, int amount, ICatalogPage page, GiftData giftData, FurnitureDefinition definition, ICatalogBundledItem bundledItem, String data) {
            String botName = "Robot";
            String botFigure = item.getPresetData();
            String botGender = "m";
            String botMotto = "O que foi, humano?";
            BotType type = BotType.GENERIC;
            BotMode mode = BotMode.RELAXED;

            switch (item.getDisplayName()) {
                case "bot_bartender":
                    type = BotType.WAITER;
                    break;

                case "bot_spy":
                    type = BotType.SPY;
                    break;
            }

            int botId = PlayerBotDao.createBot(client.getPlayer().getId(), botName, botFigure, botGender, botMotto, type);

            final IBotData botData = new PlayerBotData(
                    botId,
                    botName,
                    botMotto,
                    botFigure,
                    botGender,
                    client.getPlayer().getData().getUsername(),
                    client.getPlayer().getId(),
                    "",
                    true,
                    false,
                    7,
                    type,
                    mode,
                    ""
            );

            client.getPlayer().getBots().addBot(botData);
            client.send(new BotInventoryMessageComposer(client.getPlayer().getBots().getBots()));
            client.send(new UnseenItemsMessageComposer(new HashMap<Integer, List<Integer>>() {{
                put(5, Lists.newArrayList(botId));
            }}));

            return;
    }

    @Override
    public boolean canPurchase(ICatalogItem item, ISession client) {
        return true;
    }
}
