package com.cometproject.server.network.messages.outgoing.user.talenttrack;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.achievements.types.IAchievement;
import com.cometproject.api.game.achievements.types.IAchievementGroup;
import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.players.data.components.achievements.IAchievementProgress;
import com.cometproject.api.game.talenttrack.ITalentTrackLevel;
import com.cometproject.api.game.talenttrack.types.TalentTrackState;
import com.cometproject.api.game.talenttrack.types.TalentTrackType;
import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.game.achievements.AchievementManager;
import com.cometproject.server.game.achievements.types.Achievement;
import com.cometproject.server.protocol.headers.Composers;
import com.cometproject.server.protocol.messages.MessageComposer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TalentTrackComposer extends MessageComposer {
    private final ISession client;
    private final TalentTrackType type;

    public TalentTrackComposer(ISession client, TalentTrackType type) {
        this.client = client;
        this.type = type;
    }

    @Override
    public short getId() {
        return Composers.TalentTrackMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.type.name().toLowerCase());

        final LinkedHashMap<Integer, ITalentTrackLevel> talentTrackLevels = AchievementManager.getInstance().getTalentTrackLevels(this.type);

        if(talentTrackLevels == null) {
            msg.writeInt(0);
            return;
        }

        msg.writeInt(talentTrackLevels.size());

        for (Map.Entry<Integer, ITalentTrackLevel> set : talentTrackLevels.entrySet()) {
            try {
                final ITalentTrackLevel talentTrackLevel = set.getValue();

                msg.writeInt(talentTrackLevel.getLevel());

                TalentTrackState state = TalentTrackState.LOCKED;

                int currentLevel = this.client.getPlayer().getSettings().talentTrackLevel(this.type);

                if (currentLevel + 1 == talentTrackLevel.getLevel()) {
                    state = TalentTrackState.IN_PROGRESS;
                } else if (currentLevel >= talentTrackLevel.getLevel()) {
                    state = TalentTrackState.COMPLETED;
                }

                msg.writeInt(state.id);
                msg.writeInt(talentTrackLevel.getAchievements().size());

                final TalentTrackState finalState = state;

                talentTrackLevel.getAchievements().forEachEntry((achievement, index) -> {
                    if(achievement != null) {
                        msg.writeInt(achievement.getId());

                        msg.writeInt(index);
                        msg.writeString(achievement.getName() + index);

                        final IAchievementProgress achievementProgress = this.client.getPlayer().getAchievements().getProgress(AchievementType.getTypeByName(achievement.getName()));

                        final int progress = Math.max(0, achievementProgress != null ? achievementProgress.getProgress() : 1);

                        final IAchievementGroup achievementGroup = AchievementManager.getInstance().getAchievementGroup(AchievementType.getTypeByName(achievement.getName()));
                        IAchievement achievementLevel = achievementGroup.getAchievementByProgress(progress);

                        if(achievementLevel == null) {
                            achievementLevel = achievementGroup.getAchievement(1);
                        }

                        if(finalState != TalentTrackState.LOCKED) {
                            if(achievementLevel != null && achievementLevel.getProgressNeeded() <= progress) {
                                msg.writeInt(2);
                            } else {
                                msg.writeInt(1);
                            }
                        } else {
                            msg.writeInt(0);
                        }

                        msg.writeInt(progress);
                        msg.writeInt(achievementLevel != null ? achievementLevel.getProgressNeeded() : 0);
                    } else {
                        msg.writeInt(0);
                        msg.writeInt(0);
                        msg.writeString("");
                        msg.writeString("");
                        msg.writeInt(0);
                        msg.writeInt(0);
                        msg.writeInt(0);
                    }

                    return true;
                });

                if(talentTrackLevel.getPerks() != null && talentTrackLevel.getPerks().length > 0) {
                    msg.writeInt(talentTrackLevel.getPerks().length);

                    for(String perk : talentTrackLevel.getPerks()) {
                        msg.writeString(perk);
                    }
                } else {
                    msg.writeInt(-1);
                }

                if(!talentTrackLevel.getItems().isEmpty()) {
                    msg.writeInt(talentTrackLevel.getItems().size());

                    for(FurnitureDefinition furnitureDefinition : talentTrackLevel.getItems()) {
                        msg.writeString(furnitureDefinition.getItemName());
                        msg.writeInt(0);
                    }
                } else {
                    msg.writeInt(-1);
                }
            } catch (NoSuchElementException e) {
                return;
            }
        }
    }
}
