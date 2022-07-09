package com.cometproject.server.network.battleball.gameserver;

import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.battleball.Server;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.battleball.incoming.IncomingEventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

@WebSocket
public class GameServer {
    private static final Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @OnWebSocketConnect
    public void onConnect(Session session) throws SQLException {
        log.info("{} connected at the WebSocket server", session != null ? session.getRemoteAddress() : "null");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        HashMap<String, String> player = Server.userMap.get(session);
        Server.userMap.remove(session);
        final String username = player != null ? player.get("username") : "null";
        log.info("{} left the WebSocket server at status code {} reason: {}", username, statusCode, reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws SQLException, IllegalAccessException, InstantiationException, IOException, InvocationTargetException, NoSuchMethodException {
        final JSONObject data = new JSONObject(message);
        log.debug("WebSocket packet received='{}'", data);

        final IncomingEventManager incomingEventManager = new IncomingEventManager();
        if(data.has("header") && data.has("data")) {
            if(incomingEventManager.getEvents().containsKey(data.getInt("header"))) {
                Class<? extends IncomingEvent> eventClass = incomingEventManager.getEvents().get(data.getInt("header"));
                IncomingEvent event = eventClass.getDeclaredConstructor().newInstance();
                event.data = data;
                event.session = session;
                log.debug("Handling WebSocket packet: '{}' for session: '{}'", eventClass.getName(), session.getRemoteAddress());
                event.handle();
            } else {
                log.info("Unknow WebSocket packet [" + data.has("header") + "]");
            }
        } else {
            log.info("Invalid WebSocket packet received");
        }
    }
}

