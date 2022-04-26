package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.messages.MessageComposer;

public class StageRunningComposer extends MessageComposer {

    private final int seconds;

    public StageRunningComposer(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(seconds);
    }

    @Override
    public short getId() {
        return 5024;
    }
}
