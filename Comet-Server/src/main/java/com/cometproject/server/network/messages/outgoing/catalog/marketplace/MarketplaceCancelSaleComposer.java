package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.game.catalog.marketplace.IMarketPlaceOffer;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class MarketplaceCancelSaleComposer extends MessageComposer {
    private short id;

    private final IMarketPlaceOffer offer;
    private final boolean success;

    public MarketplaceCancelSaleComposer(IMarketPlaceOffer offer, boolean success) {
        this.id = Composers.MarketplaceCancelSaleMessageComposer;

        this.offer = offer;
        this.success = success;
    }

    @Override
    public short getId() {
        return this.id;
    }

    public void setId(final short id) {
        this.id = id;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.offer.getOfferId());
        msg.writeBoolean(this.success);
    }
}
