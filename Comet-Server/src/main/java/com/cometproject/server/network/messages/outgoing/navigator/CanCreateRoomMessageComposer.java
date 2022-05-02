package com.cometproject.server.network.messages.outgoing.navigator;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;


public class CanCreateRoomMessageComposer extends MessageComposer {
    private final int count;

    public CanCreateRoomMessageComposer(int count) {
        this.count = count;
    }

    @Override
    public short getId() {
        return Composers.CanCreateRoomMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.count >= CometSettings.roomsForUsers ? 1 : 0);
        msg.writeInt(CometSettings.roomsForUsers);
    }
}
