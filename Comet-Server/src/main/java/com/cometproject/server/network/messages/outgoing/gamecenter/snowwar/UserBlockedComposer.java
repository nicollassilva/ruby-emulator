package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.messages.MessageComposer;

public class UserBlockedComposer extends MessageComposer {

    private final int snowWarBlockedGame;

    public UserBlockedComposer(int snowWarBlockedGame) {
        this.snowWarBlockedGame = snowWarBlockedGame;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.snowWarBlockedGame);
    }

    @Override
    public short getId() {
        return 5002;
    }
}
