package com.cometproject.server.network.battleball.incoming.queues;

import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.snowwar.SnowPlayerQueue;
import com.cometproject.server.network.battleball.Server;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.sessions.Session;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

public class JoinQueueSnowstormEvent extends IncomingEvent {
    private static Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {

        HashMap<String, String> player = Server.userMap.get(this.session);
        Session client = (Session) SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(Integer.parseInt(player.get("id")));

        System.out.println("JOIN QUEUE SNOWSTORM SOCKET BITCHHH");
        SnowPlayerQueue.addPlayerInQueue(client);
    }
}
