package com.cometproject.api.game.catalog.types.subscriptions;

import com.cometproject.api.networking.messages.IComposer;

public interface IClubOffer {
    int getId();

    boolean isDeal();

    String getName();

    int getDays();

    int getCredits();

    int getPoints();

    int getPointsType();

    boolean isVip();

    void serialize(IComposer message, int hcExpireTimestamp);
}
