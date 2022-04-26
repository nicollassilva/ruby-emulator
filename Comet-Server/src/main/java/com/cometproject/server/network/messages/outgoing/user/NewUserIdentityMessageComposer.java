package com.cometproject.server.network.messages.outgoing.user;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class NewUserIdentityMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.NewUserIdentityMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);
    }
}
