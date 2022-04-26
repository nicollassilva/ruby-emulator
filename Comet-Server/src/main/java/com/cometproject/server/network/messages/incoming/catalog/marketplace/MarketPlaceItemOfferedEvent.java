package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.api.game.players.data.components.inventory.PlayerItem;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class MarketPlaceItemOfferedEvent extends MarketPlaceEvent {
    public final Session habbo;
    public final PlayerItem item;
    public int price;

    public MarketPlaceItemOfferedEvent(Session habbo, PlayerItem item, int price) {
        this.habbo = habbo;
        this.item = item;
        this.price = price;
    }


    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {

    }
}
