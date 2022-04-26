package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class OnlineCountComposer extends OutgoingExternalInterfaceMessage {
    public OnlineCountComposer(int count) {
        super("online_count");
        this.data.add("count", new JsonPrimitive(count));
    }
}
