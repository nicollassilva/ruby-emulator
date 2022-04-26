package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.SnowWar;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGame2GameObjects;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGameStatus;
import com.cometproject.server.protocol.messages.MessageComposer;

public class FullGameStatusComposer extends MessageComposer {

    private final SnowWarRoom arena;

    public FullGameStatusComposer(final SnowWarRoom arena) {
        this.arena = arena;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0); // not used
        msg.writeInt(0); // not used
        msg.writeInt(0); // not used
        SerializeGame2GameObjects.parse(msg, arena);
        msg.writeInt(0); // not used
        msg.writeInt(SnowWar.TEAMS.length);
        SerializeGameStatus.parse(msg, arena, true);
    }

    @Override
    public short getId() {
        return 5016;
    }
}
