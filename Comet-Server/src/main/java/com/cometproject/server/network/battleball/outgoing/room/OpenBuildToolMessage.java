package com.cometproject.server.network.battleball.outgoing.room;

import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import org.json.JSONObject;

import java.io.IOException;

public class OpenBuildToolMessage extends OutgoingMessage {
    @Override
    public void compose() throws IOException {
        JSONObject packet = new JSONObject();
        JSONObject data = new JSONObject();


        data.put("open", true);

        packet.put("header", Outgoing.OpenBuildToolMessage);
        packet.put("data", data);

        this.client.send(packet.toString());
    }
}
