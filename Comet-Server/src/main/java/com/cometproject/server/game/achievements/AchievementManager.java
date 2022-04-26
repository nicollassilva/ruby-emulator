package com.cometproject.server.game.achievements;

import com.cometproject.api.game.achievements.IAchievementsService;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.achievements.types.IAchievementGroup;
import com.cometproject.api.game.talenttrack.ITalentTrackLevel;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import com.cometproject.server.game.achievements.types.Achievement;
import com.cometproject.server.storage.queries.achievements.AchievementDao;
import gnu.trove.map.hash.THashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AchievementManager implements IAchievementsService {
    private static final Logger log = LogManager.getLogger(AchievementManager.class.getName());
    private static AchievementManager achievementManager;
    private final Map<AchievementType, IAchievementGroup> achievementGroups;
    private final Map<Integer, Achievement> achievements;
    private final Map<Integer, Map<AchievementType, AchievementGroup>> gameCenterAchievements;

    private final THashMap<TalentTrackType, LinkedHashMap<Integer, ITalentTrackLevel>> talentTrackLevels;

    public AchievementManager() {
        this.achievementGroups = new ConcurrentHashMap<>();
        this.achievements = new ConcurrentHashMap<>();
        this.gameCenterAchievements = new ConcurrentHashMap<>();
        this.talentTrackLevels = new THashMap<>();
    }

    public static AchievementManager getInstance() {
        if (achievementManager == null) {
            achievementManager = new AchievementManager();
        }

        return achievementManager;
    }

    @Override
    public void initialize() {
        this.loadAchievements();
        this.loadTalentTrack();

        log.info("AchievementManager initialized");
    }

    @Override
    public void loadTalentTrack() {
        if(this.talentTrackLevels.size() != 0) {
            this.talentTrackLevels.clear();
        }

        final int talentTrackCount = AchievementDao.getTalentTracks(this.talentTrackLevels);

        log.info("Loaded " + talentTrackCount + " Talent Tracks");
    }

    @Override
    public void loadAchievements() {
        if (this.achievementGroups.size() != 0) {
            for (IAchievementGroup achievementGroup : this.achievementGroups.values()) {
                if (achievementGroup.getAchievements().size() != 0) {
                    achievementGroup.getAchievements().clear();
                }
            }

            this.achievementGroups.clear();
        }

        final int achievementCount = AchievementDao.getAchievements(this.achievementGroups, this.achievements);

        log.info("Loaded " + achievementCount + " achievements (" + this.achievementGroups.size() + " groups)");
    }

    @Override
    public LinkedHashMap<Integer, ITalentTrackLevel> getTalentTrackLevels(TalentTrackType type) {
        return this.talentTrackLevels.get(type);
    }

    public Achievement getAchievementById(int id) {
        return this.achievements.get(id);
    }

    @Override
    public IAchievementGroup getAchievementGroup(AchievementType groupName) {
        return this.achievementGroups.get(groupName);
    }

    public Map<Integer, Map<AchievementType, AchievementGroup>> getGameCenterAchievementGroups() {
        return this.gameCenterAchievements;
    }

    @Override
    public Map<AchievementType, IAchievementGroup> getAchievementGroups() {
        return this.achievementGroups;
    }
}
