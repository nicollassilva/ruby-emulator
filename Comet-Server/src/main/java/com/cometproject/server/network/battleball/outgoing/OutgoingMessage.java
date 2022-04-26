package com.cometproject.server.network.battleball.outgoing;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;

public abstract class OutgoingMessage {
    public JSONObject data;
    public Session client;

    public abstract void compose() throws IOException;
}
