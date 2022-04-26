package com.cometproject.server.network.messages.incoming.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.data.SnowWarPlayerData;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class LoadStageReadyParser implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final SnowWarPlayerData snowPlayer = client.snowWarPlayerData;
        if(snowPlayer.currentSnowWar == null) {
            return;
        }

        final HumanGameObject humanObject = snowPlayer.humanObject;
        if(humanObject == null) {
            return;
        }

        //Main.in.ReadInt(); // always is 100
        snowPlayer.currentSnowWar.stageLoaded(humanObject);
    }
}
