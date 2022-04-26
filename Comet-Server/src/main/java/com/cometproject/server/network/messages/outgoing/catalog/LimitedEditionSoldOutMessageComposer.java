package com.cometproject.server.network.messages.outgoing.catalog;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class LimitedEditionSoldOutMessageComposer extends MessageComposer {
    private short id;

    public LimitedEditionSoldOutMessageComposer() {
        this.id = Composers.LimitedEditionSoldOutMessageComposer;
    }

    @Override
    public short getId() {
        return this.id;
    }

    public void setId(final short id) {
        this.id = id;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(id);
    }
}
