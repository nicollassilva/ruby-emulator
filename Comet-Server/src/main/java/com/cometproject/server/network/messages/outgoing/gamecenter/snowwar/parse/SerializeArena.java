package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.GamefuseObject;
import com.cometproject.server.game.snowwar.SnowWarRoom;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeArena {
    public static void parse(final IComposer msg, final SnowWarRoom arena) {
        msg.writeInt(arena.ArenaType.ArenaWidth);
        msg.writeInt(arena.ArenaType.ArenaHeight);
        msg.writeString(arena.ArenaType.HeightMap);
        msg.writeInt(arena.ArenaType.fuseObjects.size());
        for (final GamefuseObject fuseItem : arena.ArenaType.fuseObjects) {
            SerializeFuseObject.parse(msg, fuseItem);
        }
    }
}
