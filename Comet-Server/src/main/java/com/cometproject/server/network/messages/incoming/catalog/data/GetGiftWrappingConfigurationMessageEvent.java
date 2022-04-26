package com.cometproject.server.network.messages.incoming.catalog.data;

import com.cometproject.server.composers.catalog.data.CatalogOfferConfigMessageComposer;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.catalog.gifts.GiftWrappingConfigurationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class GetGiftWrappingConfigurationMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new GiftWrappingConfigurationMessageComposer(CatalogManager.getInstance()));
        //client.send(new CatalogOfferConfigMessageComposer());
    }
}
