package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class MentionComposer extends OutgoingExternalInterfaceMessage {
    public MentionComposer(String sender, String message, String senderLook) {
        super("mention");
        this.data.add("sender", new JsonPrimitive(sender));
        this.data.add("message", new JsonPrimitive(message));
        this.data.add("senderLook", new JsonPrimitive(senderLook));
    }
}
