package com.cometproject.server.game.players.components;

import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.achievements.types.IAchievement;
import com.cometproject.api.game.achievements.types.IAchievementGroup;
import com.cometproject.api.game.battlepass.types.BattlePassType;
import com.cometproject.api.game.battlepass.types.IBattlePass;
import com.cometproject.api.game.battlepass.types.IBattlePassHomework;
import com.cometproject.api.game.players.IPlayer;
import com.cometproject.api.game.players.data.components.PlayerBattlePass;
import com.cometproject.api.game.players.data.components.achievements.IAchievementProgress;
import com.cometproject.api.game.players.data.components.battlepass.IBattlePassProgress;
import com.cometproject.server.game.battlepass.BattlePassManager;
import com.cometproject.server.game.players.components.types.battlepass.BattlePassProgress;
import com.cometproject.server.game.players.types.PlayerComponent;
import com.cometproject.server.network.messages.outgoing.messenger.FriendToolbarNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementPointsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementProgressMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementUnlockedMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.cometproject.server.storage.queries.achievements.PlayerAchievementDao;
import com.cometproject.server.storage.queries.battlepass.PlayerBattlePassDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public class BattlePassComponent extends PlayerComponent implements PlayerBattlePass {
    private Map<BattlePassType, IBattlePassProgress> progression;

    public BattlePassComponent(IPlayer player) {
        super(player);

        this.loadBattlePass();
    }

    @Override
    public void loadBattlePass() {
        if(this.progression != null) {
            this.progression.clear();
        }

        this.progression = PlayerBattlePassDao.getBattlePassProgress(this.getPlayer().getId());
    }

    @Override
    public void progressBattlePass(BattlePassType type, int data) {
        synchronized (this) {
            IBattlePassHomework battlePassHomework = BattlePassManager.getInstance().getBattlePassHomework(type);

            if(battlePassHomework == null) {
                return;
            }

            IBattlePassProgress progress;

            if(this.progression.containsKey(type)) {
                progress = this.progression.get(type);
            } else {
                progress = new BattlePassProgress(1, 0);
                this.progression.put(type, progress);
            }

            if(battlePassHomework.getBattlePass(progress.getLevel()) == null)
                return;

            if(battlePassHomework.getBattlepass() == null)
                return;

            if(battlePassHomework.getBattlepass().size() <= progress.getLevel() && battlePassHomework.getBattlePass(progress.getLevel()).getExperiencePointsNeeded() <= progress.getProgress()) {
                return;
            }

            final int targetLevel = progress.getLevel() + 1;
            final IBattlePass currentBattlePass = battlePassHomework.getBattlePass(progress.getLevel());
            final IBattlePass targetBattlePass = battlePassHomework.getBattlePass(targetLevel);

            if(targetBattlePass == null && battlePassHomework.getLevelCount() != 1) {
                progress.setProgress(currentBattlePass.getExperiencePointsNeeded());
                PlayerBattlePassDao.saveProgressBattlePass(this.getPlayer().getId(), type, progress);

                this.getPlayer().getData().save();
                this.getPlayer().getData().increaseXpPoints(100);
                return;
            }

            int progressToGive = Math.min(currentBattlePass.getExperiencePointsNeeded(), data);
            int remainingProgress = progressToGive >= data ? 0 : data - progressToGive;

            progress.increaseProgress(progressToGive);

            if(progress.getProgress() > currentBattlePass.getExperiencePointsNeeded()) {
                int difference = progress.getProgress() - currentBattlePass.getExperiencePointsNeeded();

                progress.decreaseProgress(difference);
                remainingProgress += difference;
            }

            if(currentBattlePass.getExperiencePointsNeeded() <= progress.getProgress()) {
                this.processUnlock(currentBattlePass, battlePassHomework, progress, targetLevel);
                this.getPlayer().getMessenger().broadcast(new FriendToolbarNotificationMessageComposer(this.getPlayer().getId(), 1, battlePassHomework.getHomework() + currentBattlePass.getLevel()));
                this.getPlayer().getMessenger().sendStatus(!this.getPlayer().getSettings().getHideOnline(), this.getPlayer().getSettings().allowedFollowToRoom());
            }

            boolean hasFinishedGroup = progress.getLevel() >= battlePassHomework.getLevelCount() && progress.getProgress() >= battlePassHomework.getBattlePass(battlePassHomework.getLevelCount()).getExperiencePointsNeeded();

            if (remainingProgress != 0 && !hasFinishedGroup) {
                this.progressBattlePass(type, remainingProgress);
                return;
            }

            this.getPlayer().getData().save();
            PlayerBattlePassDao.saveProgressBattlePass(this.getPlayer().getId(), type, progress);

            this.getPlayer().flush();
        }
    }

    private void processUnlock(IBattlePass currentBattlePass, IBattlePassHomework battlePassHomework, IBattlePassProgress progress, int targetLevel) {
        this.getPlayer().getData().increaseXpPoints(currentBattlePass.getReward());
        this.getPlayer().getSession().sendQueue(new NotificationMessageComposer("generic", "Has compleado un reto del pase de batalla y se te han sumado " + currentBattlePass.getReward() + " XP"));

        this.getPlayer().poof();

        this.getPlayer().getSession().send(this.getPlayer().composeCurrenciesBalance());

        if (battlePassHomework.getBattlePass(targetLevel) != null) {
            progress.increaseLevel();
        }

        this.getPlayer().flush();
    }

    @Override
    public boolean hasStartedHomework(BattlePassType battlePassType) {
        return this.progression.containsKey(battlePassType);
    }

    @Override
    public IBattlePassProgress getProgress(BattlePassType battlePassType) {
        return this.progression.get(battlePassType);
    }

    @Override
    public void dispose() {
        this.progression.clear();
    }

    public JsonArray toJson() {
        final JsonArray coreArray = new JsonArray();

        for(Map.Entry<BattlePassType, IBattlePassProgress> achievementEntry : progression.entrySet()) {
            final JsonObject achievementObject = new JsonObject();

            achievementObject.addProperty("type", achievementEntry.getKey().getBattlePassHomework());
            achievementObject.addProperty("level", achievementEntry.getValue().getLevel());
            achievementObject.addProperty("progress", achievementEntry.getValue().getProgress());

            coreArray.add(achievementObject);
        }

        return coreArray;
    }

}
