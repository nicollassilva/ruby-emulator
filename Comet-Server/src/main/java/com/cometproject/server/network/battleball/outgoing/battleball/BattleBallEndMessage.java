package com.cometproject.server.network.battleball.outgoing.battleball;

import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import org.json.JSONObject;

import java.io.IOException;

public class BattleBallEndMessage extends OutgoingMessage {

    @Override
    public void compose() throws IOException {
        JSONObject packet = new JSONObject();


        packet.put("header", Outgoing.BattleBallEndMessage);
        packet.put("data", this.data);

        this.client.sendTextFrame(packet.toString());
    }
}
