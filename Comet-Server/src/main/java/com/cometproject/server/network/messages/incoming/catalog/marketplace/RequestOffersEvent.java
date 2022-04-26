package com.cometproject.server.network.messages.incoming.catalog.marketplace;

import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.server.game.catalog.marketplace.MarketPlace;
import com.cometproject.server.game.catalog.marketplace.MarketPlaceOffer;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.catalog.marketplace.MarketplaceOffersComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RequestOffersEvent implements Event {
    public final static Map<Integer, IMessageComposer> cachedResults = new ConcurrentHashMap<>(0);

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int min = msg.readInt();
        int max = msg.readInt();
        String query = msg.readString();
        int type = msg.readInt();

        boolean tryCache = min == -1 && max == 1 && query.isEmpty();

        if(tryCache) {
            IMessageComposer message = cachedResults.get(type);

            if(message != null) {
                client.send(message);
                return;
            }
        }

        List<MarketPlaceOffer> offers = MarketPlace.getOffers(min, max, query, type);

        IMessageComposer message = new MarketplaceOffersComposer(offers);

        if(tryCache) {
            cachedResults.put(type, message);
        }

        client.send(message);
    }
}
