package com.cometproject.server.game.players.login;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.config.CometSettings;
import com.cometproject.api.events.players.OnPlayerLoginEvent;
import com.cometproject.api.events.players.args.OnPlayerLoginEventArgs;
import com.cometproject.api.game.achievements.types.AchievementType;
import com.cometproject.api.game.players.data.components.messenger.IMessengerFriend;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.achievements.AchievementManager;
import com.cometproject.server.game.discord.Webhook;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.ModerationManager;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.modules.ModuleManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.handshake.AuthenticationOKMessageComposer;
import com.cometproject.server.network.messages.outgoing.handshake.HomeRoomMessageComposer;
import com.cometproject.server.network.messages.outgoing.landing.calendar.CampaignCalendarDataMessageComposer;
import com.cometproject.server.network.messages.outgoing.messenger.InviteFriendMessageComposer;
import com.cometproject.server.network.messages.outgoing.misc.EnableNotificationsComposer;
import com.cometproject.server.network.messages.outgoing.misc.OpenLinkMessageComposer;
import com.cometproject.server.network.messages.outgoing.misc.PingMessageComposer;
import com.cometproject.server.network.messages.outgoing.moderation.CfhTopicsInitMessageComposer;
import com.cometproject.server.network.messages.outgoing.navigator.FavouriteRoomsMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.IsFirstLoginOfDayMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.MisteryBoxDataMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.NewUserIdentityMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementPointsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.achievements.AchievementRequirementsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.buildersclub.BuildersClubMembershipMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.club.ClubStatusMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.AvailabilityStatusMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.EffectsInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.permissions.FuserightsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.wardrobe.FigureSetIdsMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.sessions.SessionManager;
import com.cometproject.server.storage.queries.player.PlayerAccessDao;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManager;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class PlayerLoginRequest implements CometTask {

    private final Session client;
    private final String ticket;


    public PlayerLoginRequest(Session client, String ticket) {
        this.client = client;
        this.ticket = ticket;
    }

    @Override
    public void run() {
        if (this.client == null) {// || this.client.getChannel().pipeline().get("encryptionDecoder") == null) {
            return;
        }

        try {
            Player player = PlayerDao.getPlayer(ticket);

            if (player == null) {
                player = PlayerDao.getPlayerFallback(ticket);

                if (player == null) {
                    client.disconnect();
                    return;
                }
            }

            final Session cloneSession = NetworkManager.getInstance().getSessions().getByPlayerId(player.getId());

            if (cloneSession != null && cloneSession.getPlayer() != null && cloneSession.getPlayer().getData() != null) {
                player.setData(cloneSession.getPlayer().getData().clone());
                player.getData().setPlayer(player);
                cloneSession.disconnect();
            }

            if (BanManager.getInstance().hasBan(Integer.toString(player.getId()), BanType.USER)
                    || BanManager.getInstance().hasBan(player.getData().getUsername(), BanType.USER)) {
                client.getLogger().warn("Banned player: " + player.getId() + " tried logging in");
                client.disconnect("banned");
                return;
            }

            player.setSession(client);
            client.setPlayer(player);

            final boolean hasTradeBan = BanManager.getInstance().hasBan(Integer.toString(player.getId()), BanType.TRADE);

            if (hasTradeBan) {
                client.getPlayer().getSettings().setAllowTrade(false);
                PlayerDao.saveAllowTrade(false, client.getPlayer().getId());
            } else if (client.getPlayer().getStats().getBans() > 0) {
                client.getPlayer().getStats().setTradeLock(0);
                client.getPlayer().getStats().save();

                client.getPlayer().getSettings().setAllowTrade(true);
                client.getPlayer().getSettings().flush();
                PlayerDao.saveAllowTrade(true, client.getPlayer().getId());
                client.getLogger().warn("Unbanned player: " + player.getId() + " with expired trade sanction.");
            }

            final String ipAddress = client.getIpAddress();

            if (ipAddress != null && !ipAddress.isEmpty()) {
                if (BanManager.getInstance().hasBan(ipAddress, BanType.IP)) {
                    client.getLogger().warn("Banned player: " + player.getId() + " tried logging in");
                    client.disconnect("banned");
                    return;
                }

                client.getPlayer().getData().setIpAddress(ipAddress);

                if (PlayerManager.getInstance().getPlayerCountByIpAddress(ipAddress) > CometSettings.maxConnectionsPerIpAddress) {
                    client.disconnect();
                    return;
                }
            }

            if (CometSettings.saveLogins)
                PlayerAccessDao.saveAccess(player.getId(), client.getUniqueId(), ipAddress);

            RoomManager.getInstance().loadRoomsForUser(player);

            if (Comet.isDebugging) {
                client.getLogger().debug(client.getPlayer().getData().getUsername() + " logged in");
            }

            player.setOnline(true);

            PlayerDao.updatePlayerStatus(player, player.isOnline(), true);

            client.sendQueue(new AuthenticationOKMessageComposer()).
                    sendQueue(new EffectsInventoryMessageComposer(player.getInventory().getEffects(), player.getInventory().getEquippedEffect())).
                    sendQueue(new FigureSetIdsMessageComposer(client.getPlayer().getWardrobe().getClothing())).
                    sendQueue(new NewUserIdentityMessageComposer()).
                    sendQueue(new FuserightsMessageComposer(client.getPlayer().getSubscription().isValid(), client.getPlayer().getData().getRank())).
                    sendQueue(new AvailabilityStatusMessageComposer(true, false, true)).
                    sendQueue(new PingMessageComposer()).
                    sendQueue(new EnableNotificationsComposer(true)).
                    sendQueue(new AchievementPointsMessageComposer(client.getPlayer().getData().getAchievementPoints())).
                    sendQueue(new IsFirstLoginOfDayMessageComposer(true)).
                    sendQueue(new MisteryBoxDataMessageComposer()).
                    sendQueue(new BuildersClubMembershipMessageComposer()).
                    sendQueue(new CfhTopicsInitMessageComposer()).
                    sendQueue(new FavouriteRoomsMessageComposer(client.getPlayer().getNavigator().getFavouriteRooms())).
                    sendQueue(new CampaignCalendarDataMessageComposer(player.getGifts())).
                    sendQueue(new ClubStatusMessageComposer(client.getPlayer().getSubscription(), ClubStatusMessageComposer.RESPONSE_TYPE_LOGIN)).
                    sendQueue(new UpdateInventoryMessageComposer()).
                    sendQueue(new AchievementRequirementsMessageComposer(AchievementManager.getInstance().getAchievementGroups().values()));

            if (!player.getPermissions().getRank().modTool()) {
                client.sendQueue(new HomeRoomMessageComposer(player.getSettings().getHomeRoom(), player.getSettings().getHomeRoom()));
            }

            if (client.getPlayer().getPermissions().getRank().modTool())
                client.sendQueue(new EmailVerificationWindowMessageComposer(1, 1));

            if (hasTradeBan)
                client.sendQueue(new NotificationMessageComposer("trade_block", Locale.getOrDefault("user.got.tradeblocked", "Se ha detectado una actividad sospechosa en tu cuenta y tus tradeos han sido bloqueados.")));


            if (CometSettings.motdEnabled) {
                client.sendQueue(new OpenLinkMessageComposer("habbopages/bienvenida.txt?" + Comet.getTime()));
            }

            if (CometSettings.onlineRewardDoubleDays.size() != 0) {
                LocalDate date = LocalDate.now();

                if (CometSettings.onlineRewardDoubleDays.contains(date.getDayOfWeek())) {
                    client.sendQueue(new MotdNotificationMessageComposer(Locale.getOrDefault("reward.double.points", "Hey %username%, \n\nToday we're giving out double points!").replace("%username%", player.getData().getUsername())));
                }
            }

            client.flush();

            if (CometExternalSettings.enableStaffMessengerLogs) {
                for (final IMessengerFriend friend : client.getPlayer().getMessenger().getFriends().values()) {
                    if (!friend.isOnline() || friend.getUserId() == client.getPlayer().getId()) {
                        continue;
                    }

                    friend.getSession().send(new NotificationMessageComposer("looks/figure/" + player.getData().getUsername(),
                            Locale.getOrDefault("player.online", "%username% está conectado!")
                                    .replace("%username%", player.getData().getUsername())));
                }
            }

            if (client.getPlayer().getSettings().getNuxStatus() == 0 && CometExternalSettings.enableStaffMessengerLogs) {
                for (Session staff : ModerationManager.getInstance().getModerators()) {
                    staff.sendQueue(new InviteFriendMessageComposer(Locale.getOrDefault("onboarding.alert", "%p acaba de registrarse en " + CometSettings.hotelName).replace("%p", client.getPlayer().getData().getUsername()), Integer.MIN_VALUE + 5000));
                }
            }

            // Process the achievements
            client.getPlayer().getAchievements().progressAchievement(AchievementType.LOGIN, 1);

            int regDate = StringUtils.isNumeric(client.getPlayer().getData().getRegDate()) ? Integer.parseInt(client.getPlayer().getData().getRegDate()) : client.getPlayer().getData().getRegTimestamp();

            if (regDate != 0) {
                final int daysSinceRegistration = (int) Math.floor((((int) Comet.getTime()) - regDate) / 86400);

                if (!client.getPlayer().getAchievements().hasStartedAchievement(AchievementType.REGISTRATION_DURATION)) {
                    client.getPlayer().getAchievements().progressAchievement(AchievementType.REGISTRATION_DURATION, daysSinceRegistration);
                } else {
                    // Progress their achievement from the last progress to now.
                    final int progress = client.getPlayer().getAchievements().getProgress(AchievementType.REGISTRATION_DURATION).getProgress();

                    if (daysSinceRegistration > client.getPlayer().getAchievements().getProgress(AchievementType.REGISTRATION_DURATION).getProgress()) {
                        final int amountToProgress = daysSinceRegistration - progress;
                        client.getPlayer().getAchievements().progressAchievement(AchievementType.REGISTRATION_DURATION, amountToProgress);
                    }
                }
            }

            if (player.getData().getAchievementPoints() < 0) {
                player.getData().setAchievementPoints(0);
                player.getData().save();
            }

            if (ModuleManager.getInstance().getEventHandler().handleEvent(OnPlayerLoginEvent.class, new OnPlayerLoginEventArgs(client.getPlayer()))) {
                client.disconnect();
            }

            if (SessionManager.isLocked) {
                client.sendQueue(new AlertMessageComposer("Hotel fechado, volte em breve!"));
                CometThreadManager.getInstance().executeSchedule(client::disconnect, 5, TimeUnit.SECONDS);
            }

            if (client.getPlayer().getData().getTimeMuted() != 0) {
                if (client.getPlayer().getData().getTimeMuted() < (int) Comet.getTime()) {
                    PlayerDao.addTimeMute(player.getData().getId(), 0);
                }
            }

            player.setSsoTicket(this.ticket);
            PlayerManager.getInstance().getSsoTicketToPlayerId().put(this.ticket, player.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
