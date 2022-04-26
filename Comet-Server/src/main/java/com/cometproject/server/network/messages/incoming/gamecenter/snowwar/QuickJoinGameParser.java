package com.cometproject.server.network.messages.incoming.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.SnowPlayerQueue;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class QuickJoinGameParser implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        SnowPlayerQueue.addPlayerInQueue(client);
    }
}