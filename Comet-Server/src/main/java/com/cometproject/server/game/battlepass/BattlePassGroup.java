package com.cometproject.server.game.battlepass;

import com.cometproject.api.game.battlepass.types.BattlePassCategory;
import com.cometproject.api.game.battlepass.types.IBattlePass;
import com.cometproject.api.game.battlepass.types.IBattlePassHomework;

import java.util.Map;

public class BattlePassGroup implements IBattlePassHomework {
    private final Map<Integer, IBattlePass> homeworks;

    private final int id;
    private final String homeworkName;
    private final BattlePassCategory category;

    public BattlePassGroup(int id, Map<Integer, IBattlePass> homeworks, String homeworkName, BattlePassCategory category) {
        this.id = id;
        this.homeworks = homeworks;
        this.homeworkName = homeworkName;
        this.category = category;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getLevelCount() {
        return this.homeworks.size();
    }

    @Override
    public IBattlePass getBattlePass(int level) {
        return this.homeworks.get(level);
    }

    @Override
    public Map<Integer, IBattlePass> getBattlepass() {
        return homeworks;
    }

    @Override
    public String getHomework() {
        return homeworkName;
    }

    @Override
    public BattlePassCategory getCategory() {
        return category;
    }
}
