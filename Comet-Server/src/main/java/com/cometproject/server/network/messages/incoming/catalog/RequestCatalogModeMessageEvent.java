package com.cometproject.server.network.messages.incoming.catalog;

import com.cometproject.server.composers.catalog.CatalogModeComposer;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.catalog.CatalogIndexMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestCatalogModeMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String catalogMode = msg.readString();

        client.send(new CatalogModeComposer(catalogMode.equalsIgnoreCase("normal") ? 0 : 1));
        client.send(new CatalogIndexMessageComposer(client.getPlayer().getData().getRank(), catalogMode));
    }
}
