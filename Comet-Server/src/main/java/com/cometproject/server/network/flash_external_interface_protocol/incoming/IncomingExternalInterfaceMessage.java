package com.cometproject.server.network.flash_external_interface_protocol.incoming;

import com.cometproject.server.network.sessions.Session;
import com.google.gson.JsonObject;

public abstract class IncomingExternalInterfaceMessage<T> {
    public final Class<T> type;

    public IncomingExternalInterfaceMessage(Class<T> type) {
        this.type = type;
    }

    public abstract void handle(Session client, T message);

    public static class JSONIncomingEvent {
        public String header;
        public JsonObject data;
    }
}

