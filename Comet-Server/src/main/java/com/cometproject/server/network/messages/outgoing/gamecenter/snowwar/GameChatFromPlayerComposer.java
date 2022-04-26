package com.cometproject.server.network.messages.outgoing.gamecenter.snowwar;

/*
 * ****************
 * @author capos *
 * ****************
 */

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GameChatFromPlayerComposer extends MessageComposer {

    private final int userId;
    private final String text;

    public GameChatFromPlayerComposer(final int userId, final String text) {
        this.userId = userId;
        this.text = text;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.userId);
        msg.writeString(this.text);
    }

    @Override
    public short getId() {
        return 5023;
    }
}
