package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceConfigComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestMarketplaceConfigEvent extends MarketPlaceEvent {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new MarketplaceConfigComposer());
    }
}
