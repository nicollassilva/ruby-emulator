package com.cometproject.api.game.catalog;

import com.cometproject.api.game.players.data.IPlayerOfferPurchase;
import com.cometproject.api.networking.messages.IComposer;

public interface ITargetOffer {
    int getId();

    String getIdentifier();

    int getPriceInCredits();

    int getPriceInActivityPoints();

    int getActivityPointsType();

    int getPurchaseLimit();

    int getExpirationTime();

    String getTitle();

    String getDescription();

    String getImageUrl();

    String getIcon();

    String[] getVars();

    int getCatalogItem();

    void serialize(IComposer message, IPlayerOfferPurchase purchase);
}
