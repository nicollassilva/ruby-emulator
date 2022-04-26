package com.cometproject.server.game.achievements.types;

import com.cometproject.api.game.achievements.types.IAchievement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Achievement implements IAchievement {
    private final int id;
    private final String name;
    private final int level;
    private final int rewardActivityPoints;
    private final int rewardType;
    private final int rewardAchievement;
    private final int progressNeeded;

    public Achievement(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("group_name");
        this.level = set.getInt("level");
        this.rewardActivityPoints = set.getInt("reward_activity_points");
        this.rewardType = set.getInt("reward_type");
        this.rewardAchievement = set.getInt("reward_achievement_points");
        this.progressNeeded = set.getInt("progress_requirement");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getRewardActivityPoints() {
        return rewardActivityPoints;
    }

    @Override
    public int getRewardAchievement() {
        return rewardAchievement;
    }

    @Override
    public int getProgressNeeded() {
        return progressNeeded;
    }

    @Override
    public int getRewardType() {
        return rewardType;
    }
}
