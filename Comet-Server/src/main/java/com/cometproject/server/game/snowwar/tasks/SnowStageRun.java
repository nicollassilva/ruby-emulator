package com.cometproject.server.game.snowwar.tasks;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.SnowWar;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.StageRunningComposer;

public class SnowStageRun {
    public static void exec(SnowWarRoom room) {
        room.broadcast(new StageRunningComposer(SnowWar.GAMESECONDS));
    }
}
