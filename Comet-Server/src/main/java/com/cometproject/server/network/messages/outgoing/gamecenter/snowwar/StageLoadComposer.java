package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.messages.MessageComposer;

public class StageLoadComposer extends MessageComposer {

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
    }

    @Override
    public short getId() {
        return 5018;
    }
}
