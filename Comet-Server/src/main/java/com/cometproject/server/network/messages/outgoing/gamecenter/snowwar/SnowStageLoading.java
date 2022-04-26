package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.SnowWar;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;

import java.util.Collection;

public class SnowStageLoading {
    public static void exec(SnowWarRoom room) {
        final Collection<HumanGameObject> playersLoaded = room.getStageLoadedPlayers();

        if(playersLoaded != null) {
            room.broadcast(new StageStillLoadingComposer(playersLoaded));

            if (!playersLoaded.isEmpty()) {
                return;
            }
        }

        for (final HumanGameObject player : room.players.values()) {
            if(!player.stageLoaded) {
                return;
            }
        }

        room.STATUS = SnowWar.STAGE_STARTING;
    }
}
