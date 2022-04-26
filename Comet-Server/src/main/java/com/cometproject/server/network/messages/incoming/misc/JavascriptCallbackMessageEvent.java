package com.cometproject.server.network.messages.incoming.misc;

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.flash_external_interface_protocol.ExternalInterfaceProtocolManager;
import com.cometproject.server.protocol.messages.MessageEvent;

public class JavascriptCallbackMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String payload = msg.readString();
        ExternalInterfaceProtocolManager.getInstance().OnMessage(payload, client);
    }
}
