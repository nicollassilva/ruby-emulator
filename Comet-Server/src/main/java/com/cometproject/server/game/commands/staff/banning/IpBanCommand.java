package com.cometproject.server.game.commands.staff.banning;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.webhooks.PunishmentWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class IpBanCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            sendNotif(Locale.getOrDefault("command.params.length", "Ops! Você fez algo errado!"), client);
            return;
        }

        final String username = params[0];
        final int length = StringUtils.isNumeric(params[1]) ? Integer.parseInt(params[1]) : 0;

        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (user == client || !user.getPlayer().getPermissions().getRank().bannable() || user.getPlayer().getPermissions().getRank().getId() >= client.getPlayer().getPermissions().getRank().getId()) {
            sendNotif(Locale.getOrDefault("command.user.notbannable", "Você não pode banir esse usuário!"), client);
            return;
        }

        final long expire = Comet.getTime() + (length * 3600L);

        final String ipAddress = user.getIpAddress();

        if (BanManager.getInstance().hasBan(ipAddress, BanType.IP)) {
            sendNotif("IP: " + ipAddress + " já está banido.", client);
            return;
        }

        BanManager.getInstance().banPlayer(BanType.IP, user.getIpAddress(), user.getPlayer().getEntity().getUsername(), length, expire, params.length > 2 ? this.merge(params, 2) : "", client.getPlayer().getId(), client.getPlayer().getEntity().getUsername(), (int) Comet.getTime(), user.getPlayer().getId());

        sendNotif("Usuário foi banido por IP. (IP: " + ipAddress + ")", client);

        final List<Integer> playerIds = PlayerManager.getInstance().getPlayerIdsByIpAddress(ipAddress);

        for (final int playerId : playerIds) {
            Session player = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (player != null) {
                player.disconnect("banned");
            }
        }

        playerIds.clear();

        PunishmentWebhook.sendIpBan(client.getPlayer().getData().getUsername(), username, params.length > 2 ? this.merge(params, 2) : "Motivo não citado", length);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "-c deu ban por IP em -d"
                .replace("-c", client.getPlayer().getData().getUsername())
                .replace("-d", username);
    }

    @Override
    public String getPermission() {
        return "ipban_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.ban", "(usuário) (tempo) (razão)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.ipban.description");
    }

    @Override
    public boolean bypassFilter() {
        return true;
    }

    @Override
    public String getLoggableDescription() {
        return this.logDesc;
    }

    @Override
    public boolean isLoggable() {
        return true;
    }

}
