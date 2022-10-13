package com.cometproject.server.network.battleball.gameserver;

import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.battleball.incoming.IncomingEventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class GameServer extends WebSocketServer {

    public static Map<WebSocket, HashMap<String, String>> userMap = new ConcurrentHashMap<>();

    private static final Logger log = LogManager.getLogger(PlayerManager.class.getName());

    public GameServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        System.out.println("WS Server started!");
        setConnectionLostTimeout(100);
        setTcpNoDelay(true);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket user, String message) {
        final JSONObject data = new JSONObject(message);

        final IncomingEventManager incomingEventManager = new IncomingEventManager();

        if (data.has("header") && data.has("data")) {
            if (incomingEventManager.getEvents().containsKey(data.getInt("header"))) {
                try {
                    final Class<? extends IncomingEvent> eventClass = incomingEventManager.getEvents().get(data.getInt("header"));
                    final IncomingEvent event;
                    event = eventClass.getDeclaredConstructor().newInstance();


                    event.data = data;
                    event.session = user;

                    event.handle();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }
}

