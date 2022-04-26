package com.cometproject.api.game.players.data.components.battlepass;

public interface IBattlePassProgress {
    void increaseProgress(int amount);

    void decreaseProgress(int amount);

    void increaseLevel();

    int getLevel();

    int getProgress();

    void setProgress(int progress);
}
