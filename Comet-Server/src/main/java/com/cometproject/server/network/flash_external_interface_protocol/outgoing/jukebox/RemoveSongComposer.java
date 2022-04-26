package com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class RemoveSongComposer extends OutgoingExternalInterfaceMessage {
    public RemoveSongComposer(int index) {
        super("remove_song");
        this.data.add("index", new JsonPrimitive(index));
    }
}
