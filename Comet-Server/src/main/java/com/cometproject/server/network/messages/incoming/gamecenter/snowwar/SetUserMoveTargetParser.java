package com.cometproject.server.network.messages.incoming.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class SetUserMoveTargetParser implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.snowWarPlayerData.currentSnowWar == null) {
            return;
        }

        final int x = msg.readInt();
        final int y = msg.readInt();

        msg.readInt(); // Turn
        msg.readInt(); // SubTurn

        if(client.snowWarPlayerData.humanObject.canWalkTo(x, y)) {
            client.snowWarPlayerData.playerMove(x, y);
        }
    }
}