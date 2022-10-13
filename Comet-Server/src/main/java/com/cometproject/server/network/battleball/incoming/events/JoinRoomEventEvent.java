package com.cometproject.server.network.battleball.incoming.events;

import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.battleball.gameserver.GameServer;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

public class JoinRoomEventEvent extends IncomingEvent {

    private static Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {

        HashMap<String, String> player = GameServer.userMap.get(this.session);
        ISession client = SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(Integer.parseInt(player.get("id")));


        if(client == null) return;

        client.send(new RoomForwardMessageComposer(this.data.getJSONObject("data").getInt("room_id")));
    }
}
