package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.snowwar.ComposerShit;
import com.cometproject.server.game.snowwar.MessageWriter;
import com.cometproject.server.game.snowwar.SnowWarRoom;
import com.cometproject.server.network.messages.outgoing.gamecenter.snowwar.parse.SerializeGameStatus;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GameStatusComposer extends MessageComposer {

    public final static MessageWriter compose(SnowWarRoom arena) {
        final MessageWriter ClientMessage = new MessageWriter(100 + (arena.gameEvents.size() * 50));

        ComposerShit.initPacket(5015, ClientMessage);
        SerializeGameStatus.parseNew(ClientMessage, arena, false);
        ComposerShit.endPacket(ClientMessage);

        return ClientMessage;
    }

    private final SnowWarRoom arena;

    public GameStatusComposer(final SnowWarRoom arena) {
        this.arena = arena;
    }

    @Override
    public void compose(IComposer msg) {
        SerializeGameStatus.parse(msg, arena, false);
    }

    @Override
    public short getId() {
        return 5015;
    }
}
