package com.cometproject.server.network.messages.incoming.catalog.targetoffer;

import com.cometproject.api.game.catalog.ITargetOffer;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.TargetOffer;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.catalog.TargetedOfferComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestTargetOfferMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final ITargetOffer offer = CatalogManager.getInstance().getTargetOffer(TargetOffer.ACTIVE_TARGET_OFFER_ID);

        if (offer != null) {
            client.send(new TargetedOfferComposer(client.getPlayer(), offer));
        }
    }
}
