package com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class ChangeVolumeComposer extends OutgoingExternalInterfaceMessage {
    public ChangeVolumeComposer(int volume) {
        super("change_volume");
        this.data.add("volume", new JsonPrimitive(volume));
    }
}
