package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.SnowWarRoom;

/**
 * Created by SpreedBlood on 2017-12-22.
 */
public class SerializeGame2SnowWarGameStats {
    public static void parse(final IComposer msg, final SnowWarRoom arena) {
        msg.writeInt(arena.MostKills.userId);
        msg.writeInt(arena.MostHits.userId);
    }
}
