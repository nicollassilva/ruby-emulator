package com.cometproject.server.network.messages.incoming.catalog.targetoffer;

import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class TargetOfferStateEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int id = msg.readInt();
        int state = msg.readInt();

        IPlayerOfferPurchase purchase = client.getPlayer().getOfferPurchase(id);

        if(purchase != null) {
            purchase.setState(state);
        }
    }
}
