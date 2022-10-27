package com.cometproject.api.game.catalog.types;

import com.cometproject.api.networking.messages.IComposer;

import java.util.List;

public interface ICatalogItem {
    void compose(IComposer msg);

    void composeClubPresents(IComposer msg);

    void serializeAvailability(IComposer msg);

    int getId();

    String getItemId();

    List<ICatalogBundledItem> getItems();

    String getDisplayName();

    int getCostCredits();

    int getCostActivityPoints();

    int getCostDiamonds();

    int getCostSeasonal();

    int getAmount();

    boolean isVip();

    int getLimitedTotal();

    int getLimitedSells();

    boolean allowOffer();

    void increaseLimitedSells(int amount);

    boolean hasBadge();

    boolean isBadgeOnly();

    String getBadgeId();

    String getPresetData();

    int getPageId();

    boolean isZeroCredits();

    boolean isZeroActivityPoints();

    boolean isZeroDiamonds();

    boolean isZeroSeasonal();

    int getOrder();

    int compareTo(ICatalogItem item);
}
