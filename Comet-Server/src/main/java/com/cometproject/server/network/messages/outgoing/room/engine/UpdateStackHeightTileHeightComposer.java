package com.cometproject.server.network.messages.outgoing.room.engine;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class UpdateStackHeightTileHeightComposer extends MessageComposer {
    public final int itemId;
    public final int height;

    public UpdateStackHeightTileHeightComposer(int itemId, int height) {
        this.itemId = itemId;
        this.height = height;
    }

    @Override
    public short getId() {
        return Composers.UpdateStackHeightTileHeightComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(itemId);
        msg.writeInt(height);
    }
}
