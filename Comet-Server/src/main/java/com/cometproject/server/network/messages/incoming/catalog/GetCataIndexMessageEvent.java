package com.cometproject.server.network.messages.incoming.catalog;

import com.cometproject.server.composers.catalog.data.CatalogOfferConfigMessageComposer;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.catalog.CatalogIndexMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class GetCataIndexMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new CatalogIndexMessageComposer(client.getPlayer().getData().getRank()));
        //client.send(new CatalogOfferConfigMessageComposer());
    }
}
