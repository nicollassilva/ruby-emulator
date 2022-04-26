package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class MarketplaceBuyErrorComposer extends MessageComposer {
    public static final int REFRESH = 1;
    public static final int SOLD_OUT = 2;
    public static final int UPDATES = 3;
    public static final int NOT_ENOUGH_CREDITS = 4;

    private final int errorCode;
    private final int unknown;
    private final int offerId;
    private final int price;

    public MarketplaceBuyErrorComposer(int errorCode, int unknown, int offerId, int price) {
        this.errorCode = errorCode;
        this.unknown = unknown;
        this.offerId = offerId;
        this.price = price;
    }

    @Override
    public short getId() {
        return Composers.MarketplaceBuyErrorMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.errorCode);
        msg.writeInt(this.unknown);
        msg.writeInt(this.offerId);
        msg.writeInt(this.price);
    }
}
