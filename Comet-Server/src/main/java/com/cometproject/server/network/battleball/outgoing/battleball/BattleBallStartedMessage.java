package com.cometproject.server.network.battleball.outgoing.battleball;

import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import org.json.JSONObject;

import java.io.IOException;

public class BattleBallStartedMessage extends OutgoingMessage {
    @Override
    public void compose() throws IOException {
        JSONObject packet = new JSONObject();


        packet.put("header", Outgoing.BattleBallStartedMessage);
        packet.put("data", this.data);

        this.client.send(packet.toString());
    }
}
