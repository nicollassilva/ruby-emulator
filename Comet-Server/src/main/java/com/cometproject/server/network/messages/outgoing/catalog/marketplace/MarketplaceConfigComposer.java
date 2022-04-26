package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class MarketplaceConfigComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.MarketplaceConfig;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(true);
        msg.writeInt(1); //Commision Percentage.
        msg.writeInt(10); //Credits
        msg.writeInt(5); //Advertisements
        msg.writeInt(1); //Min price
        msg.writeInt(1000000); //Max price
        msg.writeInt(48); //Hours in marketplace
        msg.writeInt(7); //Days to display
    }
}
