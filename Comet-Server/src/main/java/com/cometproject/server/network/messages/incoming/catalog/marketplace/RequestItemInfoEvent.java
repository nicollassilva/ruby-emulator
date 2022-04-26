package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceItemInfoComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestItemInfoEvent extends MarketPlaceEvent {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        msg.readInt();
        int id = msg.readInt();

        client.send(new MarketplaceItemInfoComposer(id));
    }
}
