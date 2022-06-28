package com.cometproject.server.network.messages.outgoing.user.pin;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class EmailVerificationWindowMessageComposer extends MessageComposer {
    private final int unknown1;
    private final int unknown2;

    public EmailVerificationWindowMessageComposer(int unknown1, int unknown2) {
        this.unknown1 = unknown1;
        this.unknown2 = unknown2;

    }

    @Override
    public short getId() {
        return Composers.EmailVerificationWindowMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.unknown1); // idk
        msg.writeInt(this.unknown2); // idk
    }
}
