package com.cometproject.server.network.messages.outgoing.catalog.recycler;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class RecyclerCompleteMessageComposer extends MessageComposer {
    public static final int RECYCLING_COMPLETE = 1;
    public static final int RECYCLING_CLOSED = 2;

    private final int code;

    public RecyclerCompleteMessageComposer(int code) {
        this.code = code;
    }

    @Override
    public short getId() {
        return Composers.RecyclerCompleteMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.code);
        msg.writeInt(0);
    }
}
