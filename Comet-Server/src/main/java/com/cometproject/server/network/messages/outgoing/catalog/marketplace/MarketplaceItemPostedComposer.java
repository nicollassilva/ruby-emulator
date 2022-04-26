package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class MarketplaceItemPostedComposer extends MessageComposer {
    public static final int POST_SUCCESS = 1;
    public static final int FAILED_TECHNICAL_ERROR = 2;
    public static final int MARKETPLACE_DISABLED = 3;
    public static final int ITEM_JUST_ADDED_TO_SHOP = 4;

    private final int code;

    public MarketplaceItemPostedComposer(int code) {
        this.code = code;
    }

    @Override
    public short getId() {
        return Composers.MarketplaceItemPosted;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.code);
    }
}
