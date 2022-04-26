package com.cometproject.server.network.messages.outgoing.misc;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class LatencyResponseComposer extends MessageComposer {
    private final int id;

    public LatencyResponseComposer(int id) {
        this.id = id;
    }

    @Override
    public short getId() {
        return Composers.LatencyResponseMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.id);
    }
}
