package com.cometproject.server.network.battleball.outgoing.handshake;

import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import org.json.JSONObject;

import java.io.IOException;

public class SSOVerifiedMessage extends OutgoingMessage {

    @Override
    public void compose() throws IOException {
        JSONObject packet = new JSONObject();
        JSONObject data = new JSONObject();


        data.put("authenticated", this.data.get("authenticated"));

        packet.put("header", Outgoing.SSOVerifiedMessage);
        packet.put("data", data);

        this.client.send(packet.toString());
    }
}
