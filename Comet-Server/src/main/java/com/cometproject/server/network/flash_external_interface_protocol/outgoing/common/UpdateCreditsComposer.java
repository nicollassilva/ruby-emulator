package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class UpdateCreditsComposer extends OutgoingExternalInterfaceMessage {
    public UpdateCreditsComposer(int credits) {
        super("update_credits");
        this.data.add("credits", new JsonPrimitive(credits));
    }
}
