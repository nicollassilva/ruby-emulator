package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.server.game.catalog.marketplace.MarketPlace;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestCreditsEvent extends MarketPlaceEvent {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        MarketPlace.getCredits(client);
    }
}
