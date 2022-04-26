package com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class PlayStopComposer extends OutgoingExternalInterfaceMessage {
    public PlayStopComposer(boolean isPlaying) {
        super("play_stop");
        this.data.add("playing", new JsonPrimitive(isPlaying));
    }
}
