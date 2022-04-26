package com.cometproject.server.network.messages.incoming.performance;

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class EventLogMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final String category = msg.readString();
        final String type = msg.readString();
        final String action = msg.readString();

        if (client.getPlayer() == null || client.getPlayer().isDisposed) {
            return;
        }

        if(action.contains("RWUAM_AMBASSADOR_KICK")) {
            client.getPlayer().getEntity().setStatusType(1);
        }

        if(action.contains("RWUAM_AMBASSADOR_MUTE_60MIN")) {
            client.getPlayer().getEntity().setStatusType(2);
        }

        if(action.contains("RWUAM_AMBASSADOR_MUTE_18HOUR")) {
            client.getPlayer().getEntity().setStatusType(3);
        }

        client.getPlayer().getEventLogCategories().add(category);
    }
}
