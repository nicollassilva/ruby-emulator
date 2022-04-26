package com.cometproject.server.game.players.components;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.achievements.types.IAchievement;
import com.cometproject.api.game.achievements.types.IAchievementGroup;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.components.PlayerAchievements;
import com.cometproject.api.game.players.data.components.achievements.IAchievementProgress;
import com.cometproject.api.game.rooms.IRoom;
import com.cometproject.server.game.achievements.AchievementManager;
import com.cometproject.server.game.players.components.types.achievements.AchievementProgress;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.players.types.PlayerComponent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.outgoing.messenger.FriendToolbarNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementPointsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementProgressMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementUnlockedMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.cometproject.server.storage.queries.achievements.PlayerAchievementDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public class AchievementComponent extends PlayerComponent implements PlayerAchievements {
    private Map<AchievementType, IAchievementProgress> progression;

    public AchievementComponent(IPlayer player) {
        super(player);

        this.loadAchievements();
    }

    @Override
    public void loadAchievements() {
        if (this.progression != null) {
            this.progression.clear();
        }

        this.progression = PlayerAchievementDao.getAchievementProgress(this.getPlayer().getId());
    }

    @Override
    public void progressAchievement(AchievementType type, int data) {
        synchronized (this) {
            IAchievementGroup achievementGroup = AchievementManager.getInstance().getAchievementGroup(type);

            if (achievementGroup == null) {
                return;
            }

            IAchievementProgress progress;

            if (this.progression.containsKey(type)) {
                progress = this.progression.get(type);
            } else {
                progress = new AchievementProgress(1, 0);
                this.progression.put(type, progress);
            }

            if (achievementGroup.getAchievement(progress.getLevel()) == null)
                return;

            if (achievementGroup.getAchievements() == null)
                return;

            if (achievementGroup.getAchievements().size() <= progress.getLevel() && achievementGroup.getAchievement(progress.getLevel()).getProgressNeeded() <= progress.getProgress()) {
                return;
            }

            final int targetLevel = progress.getLevel() + 1;
            final IAchievement currentAchievement = achievementGroup.getAchievement(progress.getLevel());
            final IAchievement targetAchievement = achievementGroup.getAchievement(targetLevel);

            if((type.name().contains("BUILDER") || type.name().contains("POSTIT")) && this.getPlayer().getEntity() != null) {
                Room room = ((Player) this.getPlayer()).getEntity().getRoom();

                if(type.getGroupName().equals("ACH_RoomDecoFurniCount")) {
                    if(room.getItems().getItemsCount() <= progress.getProgress())
                            return;
                }

                if(type.getGroupName().equals("ACH_RoomDecoHoleFurniCount")) {
                    if(room.getItems().getBlackHolesCount() <= progress.getProgress())
                            return;
                }

                if(type.getGroupName().equals("ACH_snowBoardBuild")) {
                    if(room.getItems().getSnowBoardSlopeCount() <= progress.getProgress())
                            return;
                }

                if(type.getGroupName().equals("ACH_TagA")) {
                    if(room.getItems().getIceSkateCount() <= progress.getProgress())
                            return;
                }

                if(type.getGroupName().equals("ACH_RbTagA")) {
                    if(room.getItems().getRollerSkateCount() <= progress.getProgress())
                            return;
                }

                if(type.getGroupName().equals("ACH_NotesReceived") || type.getGroupName().equals("ACH_NotesLeft")) {
                    if(room.getItems().getPostItCount() <= progress.getProgress())
                            return;
                }
            }

            if (targetAchievement == null && achievementGroup.getLevelCount() != 1) {
                progress.setProgress(currentAchievement.getProgressNeeded());
                PlayerAchievementDao.saveProgress(this.getPlayer().getId(), type, progress);

                this.getPlayer().getData().save();
                this.getPlayer().getInventory().achievementBadge(type.getGroupName(), currentAchievement.getLevel());
                return;
            }

            int progressToGive = Math.min(currentAchievement.getProgressNeeded(), data);
            int remainingProgress = progressToGive >= data ? 0 : data - progressToGive;

            progress.increaseProgress(progressToGive);

            if (progress.getProgress() > currentAchievement.getProgressNeeded()) {
                // subtract the difference and add it onto remainingProgress.
                int difference = progress.getProgress() - currentAchievement.getProgressNeeded();

                progress.decreaseProgress(difference);
                remainingProgress += difference;
            }

            if (currentAchievement.getProgressNeeded() <= progress.getProgress()) {
                this.processUnlock(currentAchievement, targetAchievement, achievementGroup, progress, targetLevel, type);
                this.getPlayer().getMessenger().broadcast(new FriendToolbarNotificationMessageComposer(this.getPlayer().getId(), 1, achievementGroup.getGroupName() + currentAchievement.getLevel()));
                this.getPlayer().getMessenger().sendStatus(!this.getPlayer().getSettings().getHideOnline(), this.getPlayer().getSettings().allowedFollowToRoom());
            } else {
                this.getPlayer().getSession().send(new AchievementProgressMessageComposer(progress, achievementGroup));
            }

            boolean hasFinishedGroup = progress.getLevel() >= achievementGroup.getLevelCount() && progress.getProgress() >= achievementGroup.getAchievement(achievementGroup.getLevelCount()).getProgressNeeded();

            if (remainingProgress != 0 && !hasFinishedGroup) {
                this.progressAchievement(type, remainingProgress);
                return;
            }

            this.getPlayer().getData().save();
            PlayerAchievementDao.saveProgress(this.getPlayer().getId(), type, progress);

            this.getPlayer().flush();
        }
    }

    private void processUnlock(IAchievement currentAchievement, IAchievement targetAchievement, IAchievementGroup achievementGroup, IAchievementProgress progress, int targetLevel, AchievementType type) {
        this.getPlayer().getData().increaseAchievementPoints(currentAchievement.getRewardAchievement());
        this.getPlayer().getData().increaseActivityPoints(currentAchievement.getRewardActivityPoints());

        this.getPlayer().poof();

        this.getPlayer().getSession().send(this.getPlayer().composeCurrenciesBalance());
        this.getPlayer().getSession().send(new UpdateActivityPointsMessageComposer(this.getPlayer().getData().getActivityPoints(), currentAchievement.getRewardAchievement()));

        if (achievementGroup.getAchievement(targetLevel) != null) {
            progress.increaseLevel();
        }

        // Achievement unlocked!
        this.getPlayer().getSession().send(new AchievementPointsMessageComposer(this.getPlayer().getData().getAchievementPoints()));
        this.getPlayer().getSession().send(new AchievementProgressMessageComposer(progress, achievementGroup));
        this.getPlayer().getSession().send(new AchievementUnlockedMessageComposer(achievementGroup.getCategory().toString(), achievementGroup.getGroupName(), achievementGroup.getId(), targetAchievement));

        this.getPlayer().getInventory().achievementBadge(type.getGroupName(), currentAchievement.getLevel());

        this.getPlayer().flush();
    }

    @Override
    public boolean hasStartedAchievement(AchievementType achievementType) {
        return this.progression.containsKey(achievementType);
    }

    @Override
    public IAchievementProgress getProgress(AchievementType achievementType) {
        return this.progression.get(achievementType);
    }

    @Override
    public void dispose() {
        this.progression.clear();
    }

    public JsonArray toJson() {
        final JsonArray coreArray = new JsonArray();

        for(Map.Entry<AchievementType, IAchievementProgress> achievementEntry : progression.entrySet()) {
            final JsonObject achievementObject = new JsonObject();

            achievementObject.addProperty("type", achievementEntry.getKey().getGroupName());
            achievementObject.addProperty("level", achievementEntry.getValue().getLevel());
            achievementObject.addProperty("progress", achievementEntry.getValue().getProgress());

            coreArray.add(achievementObject);
        }

        return coreArray;
    }
}
