package com.cometproject.server.network.flash_external_interface_protocol.outgoing;

import com.google.gson.JsonObject;

public abstract class OutgoingExternalInterfaceMessage {
    public String header;
    public JsonObject data;

    public OutgoingExternalInterfaceMessage(String name) {
        this.header = name;
        this.data = new JsonObject();
    }
}
