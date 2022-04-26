package com.cometproject.api.game.talenttrack;

import com.cometproject.api.game.achievements.types.IAchievement;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.set.hash.THashSet;

public interface ITalentTrackLevel {
    TalentTrackType getType();

    int getLevel();

    String[] getPerks();

    TObjectIntMap<IAchievement> getAchievements();

    THashSet<FurnitureDefinition> getItems();
}
