package com.cometproject.server.network.messages.outgoing.help.guides;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GuideSessionMessageMessageComposer extends MessageComposer {
    private final int userId;
    private final String message;

    public GuideSessionMessageMessageComposer(int userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    @Override
    public short getId() {
        return Composers.GuideSessionMessageMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.message);
        msg.writeInt(this.userId);
    }
}
