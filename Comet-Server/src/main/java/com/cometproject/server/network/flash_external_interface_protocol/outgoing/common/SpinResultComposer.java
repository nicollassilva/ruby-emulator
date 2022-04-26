package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class SpinResultComposer extends OutgoingExternalInterfaceMessage {
    public SpinResultComposer(int result1, int result2, int result3, boolean won, int payout) {
        super("slot_result");
        this.data.add("result1", new JsonPrimitive(result1));
        this.data.add("result2", new JsonPrimitive(result2));
        this.data.add("result3", new JsonPrimitive(result3));
        this.data.add("won", new JsonPrimitive(won));
        this.data.add("payout", new JsonPrimitive(payout));
    }
}
