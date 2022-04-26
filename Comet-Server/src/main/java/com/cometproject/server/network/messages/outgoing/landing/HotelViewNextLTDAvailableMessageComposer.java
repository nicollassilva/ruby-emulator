package com.cometproject.server.network.messages.outgoing.landing;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class HotelViewNextLTDAvailableMessageComposer extends MessageComposer {

    private final int time;
    private final int pageId;
    private final int itemId;
    private final String itemName;

    public HotelViewNextLTDAvailableMessageComposer(int time, int pageId, int itemId, String itemName) {
        this.time = time;
        this.pageId = pageId;
        this.itemId = itemId;
        this.itemName = itemName;
    }

    @Override
    public short getId() {
        return Composers.HotelViewNextLTDAvailableMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.time);
        msg.writeInt(this.pageId);
        msg.writeInt(this.itemId);
        msg.writeString(this.itemName);
    }
}
