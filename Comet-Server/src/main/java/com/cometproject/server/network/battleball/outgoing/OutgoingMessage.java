package com.cometproject.server.network.battleball.outgoing;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.io.IOException;

public abstract class OutgoingMessage {
    public JSONObject data;
    public WebSocket client;

    public abstract void compose() throws IOException;
}
