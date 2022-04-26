package com.cometproject.server.network.messages.outgoing.misc;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class EnableNotificationsComposer extends MessageComposer {
    private final boolean enabled;

    public EnableNotificationsComposer(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public short getId() {
        return Composers.EnableNotificationsComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.enabled);
    }
}
