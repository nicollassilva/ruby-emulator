package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.RoomQueue;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGame2;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GameStartedComposer extends MessageComposer {

    private final RoomQueue queue;

    public static int HEADER;

    public GameStartedComposer(RoomQueue queue) {
        this.queue = queue;
    }

    @Override
    public void compose(IComposer msg) {
        SerializeGame2.parse(msg, queue);
    }

    @Override
    public short getId() {
        return 5000;
    }
}
