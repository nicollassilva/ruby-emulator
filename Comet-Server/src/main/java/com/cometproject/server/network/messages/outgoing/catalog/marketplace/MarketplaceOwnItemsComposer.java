package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.game.catalog.marketplace.IMarketPlaceOffer;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.catalog.marketplace.MarketPlaceState;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class MarketplaceOwnItemsComposer extends MessageComposer {
    private final Session client;

    public MarketplaceOwnItemsComposer(Session client) {
        this.client = client;
    }

    @Override
    public short getId() {
        return Composers.MarketplaceOwnItemsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.client.getPlayer().getInventory().getSoldPriceTotal());
        msg.writeInt(this.client.getPlayer().getInventory().getMarketplaceItems().size());

        for (IMarketPlaceOffer offer : this.client.getPlayer().getInventory().getMarketplaceItems()) {
            try {
                if (offer.getState() == MarketPlaceState.OPEN) {
                    int offerHasExpired = (int) ((offer.getTimestamp() + 172800) - Comet.getTime());

                    if (offerHasExpired <= 0) {
                        offer.setState(MarketPlaceState.CLOSED);
                        offer.updateOffer();
                    }
                }

                msg.writeInt(offer.getOfferId());
                msg.writeInt(offer.getState().getState());
                msg.writeInt(offer.getType());
                msg.writeInt(offer.getItemId());

                if (offer.getType() == 3) {
                    msg.writeInt(offer.getLimitedNumber());
                    msg.writeInt(offer.getLimitedStack());
                } else if (offer.getType() == 2) {
                    msg.writeString("");
                } else {
                    msg.writeInt(0);
                    msg.writeString("");
                }

                msg.writeInt(offer.getPrice());

                if (offer.getState() == MarketPlaceState.OPEN)
                    msg.writeInt((int) (((offer.getTimestamp() + 172800) - Comet.getTime()) / 60));
                else
                    msg.writeInt(0);

                msg.writeInt(0);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
