package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class MarketPlaceItemSoldEvent extends MarketPlaceEvent {
    public final Player seller;
    public final Player purcharser;
    public final PlayerItem item;
    public int price;

    public MarketPlaceItemSoldEvent(Player seller, Player purcharser, PlayerItem item, int price) {
        this.seller = seller;
        this.purcharser = purcharser;
        this.item = item;
        this.price = price;
    }

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {

    }
}
