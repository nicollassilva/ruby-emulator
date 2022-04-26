package com.cometproject.server.game.players.components.types.battlepass;

import com.cometproject.api.game.players.data.components.battlepass.IBattlePassProgress;

public class BattlePassProgress implements IBattlePassProgress {
    private int level;
    private int progress;

    public BattlePassProgress(int level, int progress) {
        this.level = level;
        this.progress = progress;
    }


    @Override
    public void increaseProgress(int amount) {
        this.progress += amount;
    }

    @Override
    public void decreaseProgress(int amount) {
        this.progress -= amount;
    }

    @Override
    public void increaseLevel() {
        this.level += 1;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
    }
}
