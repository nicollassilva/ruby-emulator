package com.cometproject.server.game.catalog.marketplace;

import com.cometproject.api.game.catalog.marketplace.generic.IMarketPlaceState;

public enum MarketPlaceState implements IMarketPlaceState {
    OPEN(1),
    SOLD(2),
    CLOSED(1);

    private final int state;

    MarketPlaceState(int state) {
        this.state = state;
    }

    public static MarketPlaceState getType(int type) {
        switch (type) {
            case 1:
                return OPEN;
            case 2:
                return SOLD;
            case 3:
                return CLOSED;
        }

        return CLOSED;
    }

    public int getState() {
        return this.state;
    }
}
