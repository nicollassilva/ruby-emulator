package com.cometproject.server.game.commands.staff.muting;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.webhooks.PunishmentWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class MuteCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 2) {
            sendNotif(Locale.getOrDefault("command.mute.none", "Quem você quer mutar?"), client);
            return;
        }

        final String username = params[0];

        final int playerId = PlayerManager.getInstance().getPlayerIdByUsername(username);
        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (user.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            sendNotif(Locale.getOrDefault("command.mute.unmutable", "Você não pode mutar esse usuário!"), client);
            return;
        }

        if(username.equals(client.getPlayer().getEntity().getUsername())) {
            sendNotif(Locale.getOrDefault("command.mute.himself", "Você não pode mutar a si mesmo."), client);
            return;
        }

        try {
            final int time = Integer.parseInt(params[1]);

            if (time < 0) {
                sendNotif(Locale.getOrDefault("command.mute.negative", "Você pode usar apenas números positivos!"), client);
                return;
            }

            final int timeMuted = (int) Comet.getTime() + (time * 60);

            PlayerDao.addTimeMute(playerId, timeMuted);
            user.getPlayer().getData().setTimeMuted(timeMuted);

            final String msg = Locale.getOrDefault("command.mute.muted", "Você está mutado por violar as regras! Seu mute expira em %timeleft% minutos").replace("%timeleft%", time + "");
                user.send(new AdvancedAlertMessageComposer(msg));

            isExecuted(client);
            PunishmentWebhook.sendMute(client.getPlayer().getData().getUsername(), username, time);

            if(!CometExternalSettings.enableStaffMessengerLogs) return;

            this.logDesc = "%s mutou o usuário '%u' por %t minutos"
                    .replace("%s", client.getPlayer().getData().getUsername())
                    .replace("%u", user.getPlayer().getData().getUsername())
                    .replace("%t", Integer.toString(time));
        } catch (Exception e) {
            sendNotif(Locale.getOrDefault("command.mute.invalid", "Por favor, somente números!"), client);
        }

    }

    @Override
    public String getPermission() {
        return "mute_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.mute", "%username% %time%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.mute.description");
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
