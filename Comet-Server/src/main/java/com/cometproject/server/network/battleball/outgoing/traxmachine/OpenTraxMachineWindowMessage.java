package com.cometproject.server.network.battleball.outgoing.traxmachine;

import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import org.json.JSONObject;

import java.io.IOException;

public class OpenTraxMachineWindowMessage extends OutgoingMessage {
    @Override
    public void compose() throws IOException {
        if(this.client == null || !this.client.isOpen()) return;

        JSONObject packet = new JSONObject();

        packet.put("header", Outgoing.OpenTraxMachineWindowMessage);
        packet.put("data", this.data);

        this.client.getRemote().sendString(packet.toString());
    }
}
