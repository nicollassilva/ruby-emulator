package com.cometproject.server.network.battleball;

import com.cometproject.api.config.Configuration;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.network.battleball.gameserver.GameServer;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;

public class Server {
    public static Map<Session, HashMap<String, String>> userMap = new ConcurrentHashMap<>();

    public static void connect() {
        port(Integer.parseInt(Configuration.currentConfig().get("comet.network.customWebSocket.port")));
        ipAddress("0.0.0.0");
        webSocket("/", GameServer.class);
        init();
    }
}
