package com.cometproject.server.network.messages.outgoing.nux;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class NuxAlertComposer extends MessageComposer {
    private final int status;

    public NuxAlertComposer(int status) {
        this.status = status;
    }

    @Override
    public short getId() {
        return Composers.NuxAlertComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(status);
    }
}
