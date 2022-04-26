package com.cometproject.server.network.messages.outgoing.handshake;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class HomeRoomMessageComposer extends MessageComposer {
    private final int homeRoom;
    private final int homeToEnter;

    public HomeRoomMessageComposer(final int homeRoom, final int homeToEnter) {
        this.homeRoom = homeRoom;
        this.homeToEnter = homeToEnter;
    }

    @Override
    public short getId() {
        return Composers.NavigatorSettingsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.homeRoom);
        msg.writeInt(this.homeToEnter);
    }
}
