package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGame2GameObjects;
import com.cometproject.server.protocol.messages.MessageComposer;

public class StageStartingComposer extends MessageComposer {

    private final SnowWarRoom arena;

    public StageStartingComposer(SnowWarRoom arena) {
        this.arena = arena;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
        msg.writeString("snowwar_arena_0");
        msg.writeInt(5);
        SerializeGame2GameObjects.parse(msg, arena);
    }

    @Override
    public short getId() {
        return 5017;
    }
}
