package com.cometproject.api.game.achievements;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.achievements.types.IAchievementGroup;
import com.cometproject.api.game.talenttrack.ITalentTrackLevel;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import com.cometproject.api.utilities.Initialisable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IAchievementsService extends Initialisable {
    void loadAchievements();

    void loadTalentTrack();

    IAchievementGroup getAchievementGroup(AchievementType groupName);

    Map<AchievementType, IAchievementGroup> getAchievementGroups();

    LinkedHashMap<Integer, ITalentTrackLevel> getTalentTrackLevels(TalentTrackType type);
}
