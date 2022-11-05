package com.cometproject.server.game;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.utilities.Initialisable;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.UserObjectMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.storage.queries.system.StatisticsDao;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class GameCycle implements CometTask,   Initialisable {
    private static final int interval = 1;
    private static final Logger log = LogManager.getLogger(GameCycle.class.getName());
    private static GameCycle gameThreadInstance;
    private ScheduledFuture gameFuture;

    private boolean active = false;

    private int currentOnlineRecord = 0;
    private int onlineRecord = 0;

    public GameCycle() {

    }

    public static GameCycle getInstance() {
        if (gameThreadInstance == null)
            gameThreadInstance = new GameCycle();

        return gameThreadInstance;
    }

    @Override
    public void initialize() {
        this.gameFuture = CometThreadManager.getInstance().executePeriodic(this, interval, interval, TimeUnit.MINUTES);
        this.active = true;

        this.onlineRecord = StatisticsDao.getPlayerRecord();
    }

    @Override
    public void run() {
        try {
            if (!this.active) {
                return;
            }

            BanManager.getInstance().processBans();

            final int usersOnline = NetworkManager.getInstance().getSessions().getUsersOnlineCount();
            boolean updateOnlineRecord = false;

            if (usersOnline > this.currentOnlineRecord) {
                this.currentOnlineRecord = usersOnline;
            }

            if (usersOnline > this.onlineRecord) {
                this.onlineRecord = usersOnline;
                updateOnlineRecord = true;
            }

            this.processSession();

            if (!updateOnlineRecord)
                StatisticsDao.saveStatistics(usersOnline, RoomManager.getInstance().getRoomInstances().size(), Comet.getBuild());
            else
                StatisticsDao.saveStatistics(usersOnline, RoomManager.getInstance().getRoomInstances().size(), Comet.getBuild(), this.onlineRecord);


        } catch (Exception e) {
            log.error("Error during game thread", e);
        }
    }

    public int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    private void processSession() {
        final boolean doubleRewards = CometSettings.onlineRewardDoubleDays.contains(LocalDate.now().getDayOfWeek());
        final boolean updateDaily = this.getHour() == 0 && this.getMinute() == 0;

        if (CometSettings.onlineRewardEnabled || updateDaily) {
            final Collection<ISession> sessionValues = NetworkManager.getInstance().getSessions().getSessions().values();

            for (final ISession client : sessionValues) {
                try {
                    if (!(client instanceof Session) || client.getPlayer() == null || client.getPlayer().getData() == null) {
                        continue;
                    }

                    /*if ((Comet.getTime() - ((Session) client).getLastPing()) >= 300) {
                        client.disconnect();
                        continue;
                    }*/

                    if (updateDaily) {
                        client.getPlayer().getData().setKisses(client.getPlayer().getData().getKisses());
                        client.getPlayer().getStats().setDailyRespects(CometSettings.dailyRespects);
                        client.getPlayer().getStats().setScratches(CometSettings.dailyScratchs);

                        client.send(new UserObjectMessageComposer(((Session) client).getPlayer()));
                    }

                    client.getPlayer().managePeriodicAchievements();

                    ((Session) client).getPlayer().getAchievements().progressAchievement(AchievementType.ONLINE_TIME, 1);
                    final boolean needsReward = (Comet.getTime() - client.getPlayer().getLastReward()) >= (60L * CometSettings.onlineRewardInterval);

                    if (needsReward) {
                        final boolean clubReward = client.getPlayer().getData().getRank() == 2;

                        if (CometSettings.onlineRewardDiamonds > 0) {
                            client.getPlayer().getData().increaseVipPoints(CometSettings.onlineRewardDiamonds);
                            client.sendQueue(new NotificationMessageComposer("diamonds", String.format("Você recebeu %d diamantes!", CometSettings.onlineRewardDiamonds)));
                        }

                        final int credits = (clubReward ? CometSettings.onlineRewardCreditsVip : CometSettings.onlineRewardCredits) * (doubleRewards ? 2 : 1);
                        final int duckets = (clubReward ? CometSettings.onlineRewardDucketsVip : CometSettings.onlineRewardDuckets) * (doubleRewards ? 2 : 1);

                        client.getPlayer().getData().increaseCredits(credits);
                        client.sendQueue(new NotificationMessageComposer("cred", String.format("Você recebeu %d moedas!", credits)));

                        client.getPlayer().getData().increaseActivityPoints(duckets);
                        client.getPlayer().getSession().send(new NotificationMessageComposer("pixel", "Você recebeu " + duckets + " duckets por estar conectad" + (client.getPlayer().getData().getGender().equals("F") ? "a" : "o") + ".\n\nBônus VIP: " + (clubReward ? "Ativo" : "Desativado")));

                        client.getPlayer().getData().save();
                        client.getPlayer().sendBalance();
                        client.getPlayer().setLastReward(Comet.getTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while cycling rewards", e);
                }
            }

            if (updateDaily) {
                PlayerDao.dailyPlayerUpdate(CometSettings.dailyRespects, CometSettings.dailyScratchs);
            }
        }
    }

    public void stop() {
        this.active = false;
        this.gameFuture.cancel(false);
    }

    public int getCurrentOnlineRecord() {
        return this.currentOnlineRecord;
    }

    public int getOnlineRecord() {
        return this.onlineRecord;
    }
}
