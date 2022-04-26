package com.cometproject.server.network.messages.outgoing.landing;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class LTDCountdownMessageComposer extends MessageComposer {

    private final String c;
    private final int flush;

    public LTDCountdownMessageComposer(String c, int s) {
        this.c = c;
        this.flush = s;
    }

    @Override
    public short getId() {
        return Composers.HotelViewCustomTimerMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.c);
        msg.writeInt(this.flush);
    }
}
