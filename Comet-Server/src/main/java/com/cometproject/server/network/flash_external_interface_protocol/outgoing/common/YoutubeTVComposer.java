package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class YoutubeTVComposer extends OutgoingExternalInterfaceMessage {
    public YoutubeTVComposer(String videoId, int itemId) {
        super("youtube_tv");
        this.data.add("videoId", new JsonPrimitive(videoId));
        this.data.add("itemId", new JsonPrimitive(itemId));
    }
}
