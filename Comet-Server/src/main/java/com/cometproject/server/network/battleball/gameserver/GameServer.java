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
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        HashMap<String, String> player = Server.userMap.get(session);
        Server.userMap.remove(session);
        log.info(player.get("username") + " left the WebSocket server");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws InterruptedException, SQLException, IllegalAccessException, InstantiationException, IOException, InvocationTargetException, NoSuchMethodException {

        JSONObject data = new JSONObject(message);
        IncomingEventManager incomingEventManager = new IncomingEventManager();

        if(data.has("header") && data.has("data")) {
            if(incomingEventManager.getEvents().containsKey(data.getInt("header"))) {
                Class<? extends IncomingEvent> eventClass = incomingEventManager.getEvents().get(data.getInt("header"));
                IncomingEvent event = eventClass.getDeclaredConstructor().newInstance();
                event.data = data;
                event.session = session;
                event.handle();
            } else {
                log.info("Unknow WebSocket packet [" + data.has("header") + "]");
            }
        } else {
            log.info("Invalid WebSocket packet received");
        }

        System.out.println(message);
    }




}

