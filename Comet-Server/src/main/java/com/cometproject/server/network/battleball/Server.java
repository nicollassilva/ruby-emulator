package com.cometproject.server.network.battleball;

import com.cometproject.server.network.battleball.gameserver.GameServer;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;

public class Server {
    public static Map<Session, HashMap<String, String>> userMap = new ConcurrentHashMap<>();

    public static void connect() {
        port(30002);
        webSocket("/", GameServer.class);
        init();
    }
}
