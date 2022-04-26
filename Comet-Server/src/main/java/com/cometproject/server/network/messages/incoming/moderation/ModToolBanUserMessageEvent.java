package com.cometproject.server.network.messages.incoming.moderation;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.game.permissions.PermissionsManager;
import com.cometproject.server.game.permissions.types.Rank;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.game.players.types.PlayerStatistics;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class ModToolBanUserMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        final int userId = msg.readInt();
        final String message = msg.readString();
        final int length = msg.readInt();

        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            client.disconnect();
            return;
        }

        boolean ipBan = msg.readBoolean();
        boolean machineBan = msg.readBoolean();

        long expire = Comet.getTime() + (length * 3600);

        Session user = NetworkManager.getInstance().getSessions().getByPlayerId(userId);

        if (user == null) {
            PlayerData playerData = PlayerManager.getInstance().getDataByPlayerId(userId);

            if (playerData == null) {
                return;
            }

            final Rank playerRank = PermissionsManager.getInstance().getRank(playerData.getRank());

            if (!playerRank.bannable()) {
                client.send(new AlertMessageComposer(Locale.getOrDefault("mod.ban.nopermission", "You do not have permission to ban this player!")));
                return;
            }


            this.banPlayer(ipBan, machineBan, expire, userId, playerData.getUsername(), client.getPlayer().getId(), client.getPlayer().getEntity().getUsername(), (int) Comet.getTime(), length, message, playerData.getIpAddress(), "");
            return;
        }

        if (user == client) {
            return;
        }

        if (!user.getPlayer().getPermissions().getRank().bannable()) {
            client.send(new AlertMessageComposer(Locale.getOrDefault("mod.ban.nopermission", "You do not have permission to ban this player!")));
            return;
        }

        user.disconnect("banned");

        this.banPlayer(ipBan, machineBan, expire, user.getPlayer().getId(), user.getPlayer().getEntity().getUsername(), client.getPlayer().getId(), client.getPlayer().getEntity().getUsername(), (int) Comet.getTime(), length, message, user.getIpAddress(), machineBan ? user.getUniqueId() : "");
    }

    private void banPlayer(boolean ipBan, boolean machineBan, long expire, int playerId, String userBanned, int moderatorId, String userAddedBan, int banTime, int length, String message, String ipAddress, String uniqueId) {
        this.updateStats(playerId);

        if (ipBan) {
            BanManager.getInstance().banPlayer(BanType.IP, ipAddress, userBanned, length, expire, message, moderatorId, userAddedBan, (int) Comet.getTime());
        }

        if (machineBan && !uniqueId.isEmpty()) {
            BanManager.getInstance().banPlayer(BanType.MACHINE, uniqueId, userBanned, length, expire, message, moderatorId, userAddedBan, (int) Comet.getTime());
        }

        BanManager.getInstance().banPlayer(BanType.USER, playerId + "", userBanned, length, expire, message, moderatorId, userAddedBan, (int) Comet.getTime());
    }

    private void updateStats(int playerId) {
        PlayerStatistics playerStatistics = PlayerDao.getStatisticsById(playerId);

        if (playerStatistics != null) {
            playerStatistics.incrementBans(1);
        }
    }
}
