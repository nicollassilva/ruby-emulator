package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceOwnItemsComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestOwnItemsEvent extends MarketPlaceEvent {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new MarketplaceOwnItemsComposer(client));
    }
}
