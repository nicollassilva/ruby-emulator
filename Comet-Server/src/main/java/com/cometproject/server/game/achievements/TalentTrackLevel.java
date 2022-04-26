package com.cometproject.server.game.achievements;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.IAchievement;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.talenttrack.ITalentTrackLevel;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import com.cometproject.server.game.items.ItemManager;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TalentTrackLevel implements ITalentTrackLevel {
    private static final Logger log = LogManager.getLogger(CometSettings.class.getName());

    public TalentTrackType type;
    public int level;
    public TObjectIntMap<IAchievement> achievements;
    public THashSet<FurnitureDefinition> items;
    public String[] perks;
    public String[] badges;

    public TalentTrackLevel(ResultSet set) throws SQLException {
        this.type = TalentTrackType.valueOf(set.getString("type").toUpperCase());
        this.level = set.getInt("level");
        this.achievements = new TObjectIntHashMap<>();
        this.items = new THashSet<>();

        String[] achievements = set.getString("achievement_ids").split(",");
        String[] achievementsLevels = set.getString("achievement_levels").split(",");

        if(achievementsLevels.length == achievements.length) {
            for (int i = 0; i < achievements.length; i++) {
                if(achievements[i].isEmpty() || achievementsLevels[i].isEmpty()) {
                    continue;
                }

                IAchievement achievement = AchievementManager.getInstance().getAchievementById(Integer.parseInt(achievements[i]));

                if(achievement != null) {
                    this.achievements.put(achievement, Integer.parseInt(achievementsLevels[i]));
                } else {
                    log.error("Could not find achievement with ID " + achievements[i] + " for talenttrack level " + this.level + " of type " + this.type);
                }
            }
        }

        if(!set.getString("reward_furni").isEmpty()) {
            for(String rewardFurni : set.getString("reward_furni").split(",")) {
                FurnitureDefinition item = ItemManager.getInstance().getDefinition(Integer.parseInt(rewardFurni));

                if (item != null) {
                    this.items.add(item);
                } else {
                    log.error("Incorrect reward furni (ID: " + rewardFurni + ") for talent track level " + this.level);
                }
            }
        }


        if(!set.getString("reward_perks").isEmpty()) {
            this.perks = set.getString("reward_perks").split(",");
        }

        if(!set.getString("reward_badges").isEmpty()) {
            this.badges = set.getString("reward_badges").split(",");
        }
    }

    public TObjectIntMap<IAchievement> getAchievements() {
        return this.achievements;
    }

    public TalentTrackType getType() {
        return type;
    }

    public String[] getPerks() {
        return this.perks;
    }

    public THashSet<FurnitureDefinition> getItems() {
        return this.items;
    }

    public int getLevel() {
        return level;
    }
}
