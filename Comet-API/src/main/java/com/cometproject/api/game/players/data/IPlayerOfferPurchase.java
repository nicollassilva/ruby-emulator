package com.cometproject.api.game.players.data;

import com.cometproject.api.game.players.IPlayer;

public interface IPlayerOfferPurchase {
    int getOfferId();

    int getState();

    void setState(int state);

    int getAmount();

    void incrementAmount(int amount);

    int getLastPurchaseTimestamp();

    int getUserId();

    void setLastPurchaseTimestamp(int timestamp);

    void update(int amount, int timestamp);

    boolean needsUpdate();
}
