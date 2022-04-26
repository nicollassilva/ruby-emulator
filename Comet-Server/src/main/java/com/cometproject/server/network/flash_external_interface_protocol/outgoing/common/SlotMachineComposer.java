package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonPrimitive;

public class SlotMachineComposer extends OutgoingExternalInterfaceMessage {
    public SlotMachineComposer(int itemId, int credits) {
        super("slot_machine");
        this.data.add("itemId", new JsonPrimitive(itemId));
        this.data.add("credits", new JsonPrimitive(credits));
    }
}
