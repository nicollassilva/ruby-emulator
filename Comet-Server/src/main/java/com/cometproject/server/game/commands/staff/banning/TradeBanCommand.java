package com.cometproject.server.game.commands.staff.banning;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.webhooks.PunishmentWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class TradeBanCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            TradeBanCommand.sendNotif(Locale.getOrDefault("command.tradeban.length", "Recuerda introducir %usuario% %tiempo% %raz\u00f3n%."), client);
            return;
        }

        final String username = params[0];
        final int length = Integer.parseInt(params[1]);
        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            TradeBanCommand.sendNotif(Locale.getOrDefault("command.tradeban.offline", "El usuario est\u00e1 desconectado."), client);
            return;
        }

        if (user == client || !user.getPlayer().getPermissions().getRank().bannable() || user.getPlayer().getPermissions().getRank().getId() >= client.getPlayer().getPermissions().getRank().getId()) {
            TradeBanCommand.sendNotif(Locale.getOrDefault("command.tradeban.notbannable", "No tienes permisos para banear a este usuario."), client);
            return;
        }

        final long expire = Comet.getTime() + (length * 3600L);

        user.getPlayer().getStats().setTradeLock(expire);
        user.getPlayer().getSettings().setAllowTrade(false);
        user.getPlayer().getSettings().flush();
        user.getPlayer().getStats().addBan();
        user.getPlayer().getStats().save();

        user.send(new NotificationMessageComposer("trade_block", Locale.getOrDefault("user.got.tradeblocked", "Se ha detectado una actividad sospechosa en tu cuenta y tus tradeos han sido bloqueados durante " + length + " minutos.")));
        client.send(new NotificationMessageComposer("trade_block", Locale.getOrDefault("user.got.tradeblocked.success", "Has bloqueado correctamente los tradeos de " + username + " durante " + length + " minutos.")));

        BanManager.getInstance().banPlayer(BanType.TRADE, user.getPlayer().getId() + "", user.getPlayer().getEntity().getUsername(), length, expire, params.length > 2 ? this.merge(params, 2) : "", client.getPlayer().getId(), client.getPlayer().getEntity().getUsername(), (int) Comet.getTime());

        PunishmentWebhook.sendTradeBan(client.getPlayer().getData().getUsername(), username, params.length > 2 ? this.merge(params, 2) : "Motivo não citado", length);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "El Staff -c ha bloqueado los tradeos a -d por -e minutos.".replace("-c", client.getPlayer().getData().getUsername()).replace("-d", user.getPlayer().getData().getUsername()).replace("-e", Integer.toString(length));
    }

    @Override
    public String getPermission() {
        return "tradeban_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.ban", "%username% %time% %reason%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.tradeban.description");
    }

    @Override
    public boolean bypassFilter() {
        return true;
    }

    @Override
    public String getLoggableDescription() {
        return this.logDesc;
    }
}

