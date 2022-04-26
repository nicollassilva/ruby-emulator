package com.cometproject.server.network.messages.outgoing.catalog.recycler;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class ReloadRecyclerMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.ReloadRecyclerMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);
        msg.writeInt(0);
    }
}
