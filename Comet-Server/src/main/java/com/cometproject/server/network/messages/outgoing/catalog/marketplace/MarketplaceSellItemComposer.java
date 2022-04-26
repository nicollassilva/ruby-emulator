package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class MarketplaceSellItemComposer extends MessageComposer {
    public static final int NOT_ALLOWED = 2;
    public static final int NO_TRADE_PASS = 3;
    public static final int NO_ADS_LEFT = 4;

    private final int errorCode;
    private final int valueA;
    private final int valueB;

    public MarketplaceSellItemComposer(int errorCode, int valueA, int valueB) {
        this.errorCode = errorCode;
        this.valueA = valueA;
        this.valueB = valueB;
    }

    @Override
    public short getId() {
        return Composers.MarketplaceSellItemMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.errorCode);
        msg.writeInt(this.valueA);
        msg.writeInt(this.valueB);
    }
}
