package com.cometproject.server.network.battleball.incoming.battleball;

import com.cometproject.server.game.gamecenter.games.battleball.player.BattleBallPlayerQueue;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.battleball.gameserver.GameServer;
import com.cometproject.server.network.battleball.incoming.IncomingEvent;
import com.cometproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

public class BattleBallLeaveEvent extends IncomingEvent {

    private static final Logger log = LogManager.getLogger(PlayerManager.class.getName());

    @Override
    public void handle() throws SQLException, IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {

        HashMap<String, String> player = GameServer.userMap.get(this.session);
        //Session client = SessionManagerAccessor.getInstance().getSessionManager().fromPlayer(Integer.parseInt(player.get("id")));
        Session client = NetworkManager.getInstance().getSessions().fromPlayer(player.get("id"));
        if(BattleBallPlayerQueue.playerExit((Session) client)) {
            System.out.println("WAS IN QUEUE");
        } else {
            if(client == null) return;

            System.out.println("WAS NOT IN QUEUE");
            client.send(new HotelViewMessageComposer());
        }
    }
}
