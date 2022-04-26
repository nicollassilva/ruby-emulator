package com.cometproject.api.game.battlepass.types;

import java.util.Map;

public interface IBattlePassHomework {
    int getId();

    int getLevelCount();

    IBattlePass getBattlePass(int level);

    Map<Integer, IBattlePass> getBattlepass();

    String getHomework();

    BattlePassCategory getCategory();
}
