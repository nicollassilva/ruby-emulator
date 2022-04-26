package com.cometproject.server.network.messages.incoming.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class RequestFullStatusUpdateParser implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final SnowWarRoom room = client.snowWarPlayerData.currentSnowWar;
        if (room == null) {
            return;
        }

        room.fullGameStatusQueue.add(client.getChannel());
    }
}
