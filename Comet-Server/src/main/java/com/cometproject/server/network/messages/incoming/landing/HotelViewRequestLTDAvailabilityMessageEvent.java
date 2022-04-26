package com.cometproject.server.network.messages.incoming.landing;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.landing.HotelViewNextLTDAvailableMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class HotelViewRequestLTDAvailabilityMessageEvent  implements Event {
    public static boolean ENABLED = true;
    public static int TIMESTAMP;
    public static int ITEM_ID;
    public static int PAGE_ID;
    public static String ITEM_NAME;

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if(ENABLED) {
            int timeRemaining = Math.max(TIMESTAMP - (int)Comet.getTime(), 1);

            client.getPlayer().getSession().send(new HotelViewNextLTDAvailableMessageComposer(
                    timeRemaining,
                    timeRemaining > 0 ? -1 : ITEM_ID,
                    timeRemaining > 0 ? -1 : PAGE_ID,
                    timeRemaining > 0 ? "" : ITEM_NAME));
        }
    }
}

