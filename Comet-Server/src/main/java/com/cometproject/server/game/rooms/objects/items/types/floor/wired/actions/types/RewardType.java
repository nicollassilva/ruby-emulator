package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.types;

import java.util.HashMap;
import java.util.Map;

public enum RewardType {
    CREDITS("moedas"),
    ACTIVITY_POINTS("duckets"),
    VIP_POINTS("diamantes"),
    SEASONAL_POINTS("rubis"),
    GO_TO_ROOM("goto"),
    ALERT("alerta");


    private static final Map<String, RewardType> map = new HashMap<>();

    private final String currency;

    RewardType(String currency) {
        this.currency = currency;
    }

    static {
        for (RewardType rewardType : RewardType.values()) {
            map.put(rewardType.currency, rewardType);
        }
    }

    public static RewardType getCurrencyTypeByKey(String str) {
        return map.get(str);
    }

    public String getCurrency() {
        return this.currency;
    }
}
