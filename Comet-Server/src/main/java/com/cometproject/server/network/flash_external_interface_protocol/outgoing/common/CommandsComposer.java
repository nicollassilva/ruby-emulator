package com.cometproject.server.network.flash_external_interface_protocol.outgoing.common;

import com.cometproject.server.network.flash_external_interface_protocol.outgoing.OutgoingExternalInterfaceMessage;
import com.google.gson.JsonArray;

import java.util.List;

public class CommandsComposer extends OutgoingExternalInterfaceMessage {
    public CommandsComposer(List<String> commands) {
        super("commands");
        JsonArray json_cmd = new JsonArray();
        for (String c : commands) {
            json_cmd.add(c);
        }
        this.data.add("commands", json_cmd);
    }
}
