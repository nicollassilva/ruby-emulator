package com.cometproject.server.network.messages.outgoing.user.club;

import com.cometproject.api.game.catalog.types.subscriptions.IClubOffer;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.catalog.CatalogManager;
import com.cometproject.server.game.catalog.subscriptions.ClubOffer;
import com.cometproject.server.game.players.components.SubscriptionComponent;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;
import gnu.trove.map.hash.THashMap;

import java.util.Calendar;
import java.util.List;


public class ClubDataMessageComposer extends MessageComposer {
    private final SubscriptionComponent subscriptionComponent;
    private final int windowId;

    public ClubDataMessageComposer(final SubscriptionComponent subscriptionComponent, final int windowId) {
        this.subscriptionComponent = subscriptionComponent;
        this.windowId = windowId;
    }

    @Override
    public short getId() {
        return Composers.ClubDataMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        List<IClubOffer> offers = CatalogManager.getInstance().getClubOfferItems();

        msg.writeInt(offers.size()); // size

        for (IClubOffer offer : offers) {
            offer.serialize(msg, subscriptionComponent.getExpire());
        }

        msg.writeInt(this.windowId);
    }
}