package com.cometproject.server.network.battleball.incoming;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public abstract class IncomingEvent {

    public JSONObject data;
    public Session session;
    public abstract void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException;

}
