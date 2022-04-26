package com.cometproject.server.network.messages.outgoing.user.citizenship;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class CitizenshipStatusMessageComposer extends MessageComposer {
    private final String name;

    public CitizenshipStatusMessageComposer(String name) {
        this.name = name;
    }

    @Override
    public short getId() {
        return Composers.UserCitizenshipMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.name);
        msg.writeInt(4);
        msg.writeInt(4);
    }
}
