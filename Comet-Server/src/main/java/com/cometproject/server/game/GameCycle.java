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


public class GameCycle implements CometTask, Initialisable {
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

    private void processSession() {

        final LocalDate date = LocalDate.now();
        final Calendar calendar = Calendar.getInstance();

        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        boolean clubReward = false;

        final boolean doubleRewards = CometSettings.onlineRewardDoubleDays.contains(date.getDayOfWeek());
        final boolean updateDaily = hour == 0 && minute == 0;
        final int dailyRespects = 3;
        final int dailyScratches = 3;

        if (CometSettings.onlineRewardEnabled || updateDaily) {
            final Collection<ISession> sessionValues = NetworkManager.getInstance().getSessions().getSessions().values();

            for (final ISession client : sessionValues) {
                try {
                    if (!(client instanceof Session) || client.getPlayer() == null || client.getPlayer().getData() == null) {
                        continue;
                    }

                    if (client.getPlayer().getSubscription() != null && client.getPlayer().getSubscription().isValid()) {
                        clubReward = true;
                    }

                    /*if ((Comet.getTime() - ((Session) client).getLastPing()) >= 300) {
                        client.disconnect();
                        continue;
                    }*/

                    if (updateDaily) {
                        //  TODO: put this in config.
                        client.getPlayer().getData().setKisses(client.getPlayer().getData().getKisses());
                        client.getPlayer().getStats().setDailyRespects(dailyRespects);
                        client.getPlayer().getStats().setScratches(dailyScratches);

                        client.send(new UserObjectMessageComposer(((Session) client).getPlayer()));
                    }

                    client.getPlayer().managePeriodicAchievements();

                    ((Session) client).getPlayer().getAchievements().progressAchievement(AchievementType.ONLINE_TIME, 1);
                    final boolean needsReward = (Comet.getTime() - client.getPlayer().getLastReward()) >= (60L * CometSettings.onlineRewardInterval);

                    if (needsReward) {
                        if (CometSettings.onlineRewardDiamonds > 0) {
                            client.getPlayer().getData().increaseVipPoints(CometSettings.onlineRewardDiamonds);
                            client.sendQueue(new NotificationMessageComposer("diamonds", String.format("Você recebeu %d diamantes!", CometSettings.onlineRewardDiamonds)));
                        }

                        if (CometSettings.onlineRewardCredits > 0) {
                            client.getPlayer().getData().increaseCredits(CometSettings.onlineRewardCredits * (doubleRewards ? 2 : 1) * (clubReward ? 2 : 1));
                            client.sendQueue(new NotificationMessageComposer("cred", String.format("Você recebeu %d moedas!", CometSettings.onlineRewardCredits)));
                        }

                        if (CometSettings.onlineRewardDuckets > 0) {
                            client.getPlayer().getData().increaseActivityPoints(CometSettings.onlineRewardDuckets * (doubleRewards ? 2 : 1) * (clubReward ? 2 : 1));
                            client.getPlayer().getSession().send(new NotificationMessageComposer("pixel", "Você recebeu " + CometSettings.onlineRewardDuckets * (doubleRewards ? 2 : 1) * (clubReward ? 2 : 1) + " duckets por estar conectad" + (client.getPlayer().getData().getGender().equals("F") ? "a" : "o") + ".\n\nBônus VIP: " + (clubReward ? "Ativo" : "Desativado") + "\nBônus Especial: " + (doubleRewards ? "Ativo" : "Desativado")));
                        }

                        final PlayerData playerData = (PlayerData) client.getPlayer().getData();
                        playerData.increaseBonusPoints(1);
                        client.getPlayer().getData().save();

                        client.getPlayer().setLastReward(Comet.getTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while cycling rewards", e);
                }
            }

            if (updateDaily) {
                PlayerDao.dailyPlayerUpdate(dailyRespects, dailyScratches);
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
