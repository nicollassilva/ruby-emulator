package com.cometproject.storage.api.data.rewards;

public class RewardData {
    private final String code;
    private final String badge;
    private final int diamonds;
    private int seasonal;

    public RewardData(String code, String badge, int diamonds, int seasonal) {
        this.code = code;
        this.badge = badge;
        this.diamonds = diamonds;
    }

    public String getCode() {
        return code;
    }

    public String getBadge() {
        return badge;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public int getSeasonal() {
        return seasonal;
    }
}
