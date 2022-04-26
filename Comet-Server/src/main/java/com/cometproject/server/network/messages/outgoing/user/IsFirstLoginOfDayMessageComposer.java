package com.cometproject.server.network.messages.outgoing.user;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class IsFirstLoginOfDayMessageComposer extends MessageComposer {
    private final boolean isFirstLoginOfDay;

    public IsFirstLoginOfDayMessageComposer(boolean isFirstLoginOfDay) {
        this.isFirstLoginOfDay = isFirstLoginOfDay;
    }
    @Override
    public short getId() {
        return Composers.IsFirstLoginOfDayComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.isFirstLoginOfDay);
    }
}
