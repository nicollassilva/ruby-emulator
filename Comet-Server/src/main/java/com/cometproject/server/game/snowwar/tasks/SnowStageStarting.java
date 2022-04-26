package com.cometproject.server.game.snowwar.tasks;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.game.snowwar.gameobjects.GameItemObject;
import com.cometproject.server.game.snowwar.gameobjects.HumanGameObject;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.StageStartingComposer;

import java.util.ArrayList;

public class SnowStageStarting {
    public static void exec(SnowWarRoom room) {
        room.gameObjects.clear();

        room.ArenaType.gameObjects(room.gameObjects, room);

        for (final GameItemObject obj : room.gameObjects.values()) {
            // TODO: use "addGameObject" in ArenaType.gameObjects and set objectId
            obj._active = true;
            obj.objectId = room.objectIdCounter++;
        }

        for (final HumanGameObject player : room.players.values()) {
            room.addGameObject(player);
        }

        room.checksum = 0;
        for (final GameItemObject Object : room.gameObjects.values()) {
            Object.GenerateCHECKSUM(room, 1);
        }

        room.broadcast(new StageStartingComposer(room));

        room.fullGameStatusQueue = new ArrayList<>();
    }
}
