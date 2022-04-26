package com.cometproject.server.network.battleball.outgoing.battlepass;

import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import org.json.JSONObject;

import java.io.IOException;

public class OpenBattlePassMessage extends OutgoingMessage {
    @Override
    public void compose() throws IOException {
        JSONObject packet = new JSONObject();

        packet.put("header", Outgoing.OpenBattlePassMessage);
        packet.put("data", this.data);

        this.client.getRemote().sendString(packet.toString());
    }
}
