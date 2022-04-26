package com.cometproject.server.network.messages.incoming.user;

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.user.IgnoredUsersComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class UnknowEvent1 implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new IgnoredUsersComposer());
    }
}
