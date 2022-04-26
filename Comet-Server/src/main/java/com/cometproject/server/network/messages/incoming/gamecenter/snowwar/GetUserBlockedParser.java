package com.cometproject.server.network.messages.incoming.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.UserBlockedComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class GetUserBlockedParser implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new UserBlockedComposer(0));
    }
}
