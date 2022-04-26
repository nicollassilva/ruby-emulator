package com.cometproject.api.game.furniture.types;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrackableReward {
    private final int hitRequirement;
    private final CrackableRewardType rewardType;
    private final CrackableType crackableType;
    private final int requiredEffect;
    private final String rewardData;
    private int totalChance;
    private final Map<Integer, Map.Entry<Integer, Integer>> rewards;

    public CrackableReward(int hitRequirement, CrackableRewardType rewardType, CrackableType crackableType, String rewardData, int required_effect) {
        this.hitRequirement = hitRequirement;
        this.rewardType = rewardType;
        this.crackableType = crackableType;
        this.requiredEffect = required_effect;
        this.rewardData = rewardData;
        this.rewards = new HashMap<>();
        this.initializeRewards();
    }

    private void initializeRewards() {
        if (rewardData.isEmpty()) return;

        String[] rwds = this.rewardData.split(";");
        this.totalChance = 0;
        for(String prize : rwds) {
            int itemId = 0;
            int chance = 100;

            if (prize.contains(":") && prize.split(":").length == 2) {
                itemId = Integer.parseInt(prize.split(":")[0]);
                chance = Integer.parseInt(prize.split(":")[1]);
            } else if (prize.contains(":")) {
                Logger.getGlobal().log(Level.WARNING, "Invalid configuration of crackable prizes '" + prize + "' format should be itemId:chance.");
            } else {
                itemId = Integer.parseInt(prize.replace(":", ""));
            }
            this.rewards.put(itemId, new AbstractMap.SimpleEntry<>(totalChance, totalChance + chance));
            totalChance += chance;
        }
    }

    public int getHitRequirement() {
        return hitRequirement;
    }

    public CrackableRewardType getRewardType() {
        return rewardType;
    }

    public CrackableType getCrackableType() {
        return crackableType;
    }

    public String getRewardData() {
        return rewardData;
    }

    public int getRequiredEffect() {
        return requiredEffect;
    }

    public int getRandomReward() {
        if(this.rewards.size() == 0) return 0;

        Random random = new Random();
        int result = random.nextInt(totalChance);
        int notFound = 0;

        for (Map.Entry<Integer, Map.Entry<Integer, Integer>> set : this.rewards.entrySet()) {
            notFound = set.getKey();
            if (result >= set.getValue().getKey() && result < set.getValue().getValue()) {
                return set.getKey();
            }
        }

        return notFound;
    }
}
