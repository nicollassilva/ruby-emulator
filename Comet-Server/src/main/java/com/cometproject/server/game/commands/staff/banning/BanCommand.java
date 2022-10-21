package com.cometproject.server.game.commands.staff.banning;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.webhooks.PunishmentWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;

public class BanCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            sendNotif(Locale.getOrDefault("command.params.length", "Ops! Você fez algo errado!"), client);
            return;
        }

        final String username = params[0];
        final int length = Integer.parseInt(params[1]);

        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (!StringUtils.isNumeric(String.valueOf(length))) {
            return;
        }

        if (user == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (user == client || !user.getPlayer().getPermissions().getRank().bannable() || user.getPlayer().getPermissions().getRank().getId() >= client.getPlayer().getPermissions().getRank().getId()) {
            sendNotif(Locale.getOrDefault("command.user.notbannable", "Você não pode banir esse usuário!"), client);
            return;
        }

        client.getPlayer().getStats().addBan();

        user.disconnect("banned");

        final long expire = Comet.getTime() + (length * 3600L);
        final int userId = user.getPlayer().getId();

        BanManager.getInstance().banPlayer(BanType.USER, userId + "", user.getPlayer().getEntity().getUsername(), length, expire, params.length > 2 ? this.merge(params, 2) : "", client.getPlayer().getId(), client.getPlayer().getEntity().getUsername(), (int) Comet.getTime(), userId);

        PunishmentWebhook.sendBan(client.getPlayer().getData().getUsername(), username, params.length > 2 ? this.merge(params, 2) : "Motivo não citado", length);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "-c baniu o usuário -d por -e minutos"
                .replace("-c", client.getPlayer().getData().getUsername())
                .replace("-d", user.getPlayer().getData().getUsername())
                .replace("-e", Integer.toString(length));
    }

    @Override
    public String getPermission() {
        return "ban_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.ban", "(usuário) (tempo) (razão)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.ban.description");
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
