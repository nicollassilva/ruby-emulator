package com.cometproject.server.network.messages.incoming.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class MakeSnowballParser implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        msg.readInt(); // Turn
        msg.readInt(); // SubTurn
        client.snowWarPlayerData.makeSnowBall();
    }
}
