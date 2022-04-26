package com.cometproject.server.network.messages.incoming.performance;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.misc.JavascriptCallbackMessageComposer;
import com.cometproject.server.network.messages.outgoing.misc.LatencyResponseComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.outgoing.common.OnlineCountComposer;
import com.cometproject.server.protocol.messages.MessageEvent;


public class RequestLatencyTestMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.setLastPing(Comet.getTime());
        client.send(new LatencyResponseComposer(msg.readInt()));

        // send an update of online count as well
        int count = NetworkManager.getInstance().getSessions().getUsersOnlineCount();
        client.send(new JavascriptCallbackMessageComposer(new OnlineCountComposer(count)));
    }
}
