package com.cometproject.server.network.messages.outgoing.help.guides;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class GuideSessionInvitedToGuideRoomMessageComposer extends MessageComposer {
    private final int id;
    private final String roomName;

    public GuideSessionInvitedToGuideRoomMessageComposer(int roomId, String roomName) {
        this.id = roomId;
        this.roomName = roomName;
    }

    @Override
    public short getId() {
        return Composers.GuideSessionInvitedToGuideRoomMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(id);
        msg.writeString(roomName);
    }
}
