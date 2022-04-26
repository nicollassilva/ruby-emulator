package com.cometproject.server.network.messages.incoming.user;

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestUserCreditsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(client.getPlayer().composeCreditBalance());
        client.send(client.getPlayer().composeCurrenciesBalance());
    }
}
