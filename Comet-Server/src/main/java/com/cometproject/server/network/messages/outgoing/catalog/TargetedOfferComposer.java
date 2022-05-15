package com.cometproject.server.network.messages.outgoing.catalog;

import com.cometproject.api.game.catalog.ITargetOffer;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.game.players.PlayerOfferPurchase;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

public class TargetedOfferComposer extends MessageComposer {
    private final IPlayer player;
    private final ITargetOffer offer;

    public TargetedOfferComposer(IPlayer player, ITargetOffer offer) {
        this.player = player;
        this.offer = offer;
    }

    @Override
    public short getId() {
        return Composers.TargetedOffer;
    }

    @Override
    public void compose(IComposer msg) {
        final IPlayerOfferPurchase purchase = PlayerOfferPurchase.getOrCreate(this.player, this.offer.getId());

        this.offer.serialize(msg, purchase);
    }
}
