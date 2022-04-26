package com.cometproject.api.game.catalog.marketplace;

import com.cometproject.api.game.catalog.marketplace.generic.IMarketPlaceState;

public interface IMarketPlaceOffer {

    int getOfferId();

    void setOfferId(int offerId);

    int getItemId();

    int getPrice();

    int getCount();

    IMarketPlaceState getState();

    void setState(IMarketPlaceState state);

    int getTimestamp();

    int getAvarage();

    int getSoldTimestamp();

    void setSoldTimestamp(int soldTimestamp);

    int getLimitedStack();

    int getLimitedNumber();

    int getSoldItemId();

    void needsUpdate(boolean value);

    int getType();

    void updateOffer();
}
