package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.server.game.catalog.marketplace.MarketPlaceOffer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class MarketPlaceItemCancelledEvent extends MarketPlaceEvent {
    public MarketPlaceOffer offer;

    public MarketPlaceItemCancelledEvent(MarketPlaceOffer offer) {
        this.offer = offer;
    }

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {

    }
}
