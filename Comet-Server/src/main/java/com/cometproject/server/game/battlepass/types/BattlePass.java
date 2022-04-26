package com.cometproject.server.game.battlepass.types;

import com.cometproject.api.game.battlepass.types.IBattlePass;

public class BattlePass implements IBattlePass {
    private final int level;
    private final int reward;
    private final int experiencePointsNeeded;

    public BattlePass(int level, int reward, int experiencePointsNeeded) {
        this.level = level;
        this.reward = reward;
        this.experiencePointsNeeded = experiencePointsNeeded;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getReward() {
        return reward;
    }

    @Override
    public int getExperiencePointsNeeded() {
        return experiencePointsNeeded;
    }
}
