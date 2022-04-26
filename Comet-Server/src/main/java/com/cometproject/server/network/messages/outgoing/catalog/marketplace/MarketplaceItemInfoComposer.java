package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.catalog.marketplace.MarketPlace;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class MarketplaceItemInfoComposer extends MessageComposer {
    private final int itemId;

    public MarketplaceItemInfoComposer(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public short getId() {
        return Composers.MarketplaceItemInfoMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        MarketPlace.serializeItemInfo(this.itemId, msg);
    }
}
