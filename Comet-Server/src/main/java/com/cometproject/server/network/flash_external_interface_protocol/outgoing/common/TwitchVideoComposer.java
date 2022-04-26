package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class TwitchVideoComposer extends OutgoingExternalInterfaceMessage {
    public TwitchVideoComposer(String videoId) {
        super("twitch");
        this.data.add("videoId", new JsonPrimitive(videoId));
    }
}
