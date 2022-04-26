package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;

public class OpenMentionBoxComposer extends OutgoingExternalInterfaceMessage {
    public OpenMentionBoxComposer() {
        super("open_mentions");
    }
}
