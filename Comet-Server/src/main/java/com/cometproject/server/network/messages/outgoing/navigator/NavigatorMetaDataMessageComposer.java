package com.cometproject.server.network.messages.outgoing.navigator;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class NavigatorMetaDataMessageComposer extends MessageComposer {
    private static final String[] categories = new String[] {"official_view", "hotel_view", "roomads_view", "myworld_view"};

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(categories.length);
        for(String category : categories) {
            msg.writeString(category);
            msg.writeInt(0);
        }
    }

    @Override
    public short getId() {
        return Composers.NavigatorMetaDataParserMessageComposer;
    }
}
