package com.cometproject.server.network.flash_external_interface_protocol.outgoing.jukebox;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;

public class DisposePlaylistComposer extends OutgoingExternalInterfaceMessage {
    public DisposePlaylistComposer() {
        super("dispose_playlist");
    }
}
