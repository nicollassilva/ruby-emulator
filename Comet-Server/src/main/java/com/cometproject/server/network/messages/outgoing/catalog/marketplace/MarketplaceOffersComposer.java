package com.cometproject.server.network.messages.outgoing.catalog.marketplace;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.catalog.marketplace.MarketPlace;
import com.cometproject.server.game.catalog.marketplace.MarketPlaceOffer;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.List;

public class MarketplaceOffersComposer extends MessageComposer {
    private final List<MarketPlaceOffer> offers;

    public MarketplaceOffersComposer(List<MarketPlaceOffer> offers) {
        this.offers = offers;
    }

    @Override
    public short getId() {
        return Composers.MarketplaceOffers;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.offers.size());

        for(final MarketPlaceOffer offer : this.offers) {
            msg.writeInt(offer.getOfferId());
            msg.writeInt(1);
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

            msg.writeInt(MarketPlace.calculateCommision(offer.getPrice()));
            msg.writeInt(0);
            msg.writeInt(MarketPlace.calculateCommision(offer.getAvarage()));
            msg.writeInt(offer.getCount());
        }

        msg.writeInt(this.offers.size());
    }
}
