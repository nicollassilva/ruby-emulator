package com.cometproject.server.network.messages.outgoing.user.friends;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class FriendFindingRoomComposer extends MessageComposer {
    public static final int NO_ROOM_FOUND = 0;
    public static final int ROOM_FOUND = 1;

    public final int errorCode;

    public FriendFindingRoomComposer(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public short getId() {
        return Composers.FindFriendsProcessResultMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.errorCode);
    }
}
