package com.cometproject.server.network.battleball.outgoing.messenger;

import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import org.json.JSONObject;

import java.io.IOException;

public class ToggleFriendListMessage extends OutgoingMessage {
    @Override
    public void compose() throws IOException {
        JSONObject packet = new JSONObject();


        packet.put("header", Outgoing.ToggleFriendListMessage);
        packet.put("data", this.data);

        this.client.sendTextFrame(packet.toString());
    }
}
