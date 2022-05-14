package com.cometproject.server.game.commands.staff.alerts;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class AlertCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            sendNotif(Locale.getOrDefault("command.alert.none", "Para quem você deseja enviar um alerta?"), client);
            return;
        }

        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(params[0]);

        if (user == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        user.send(new AlertMessageComposer(this.merge(params, 1)));

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "-c enviou um alerta para -d. [-e]"
                .replace("-c", client.getPlayer().getData().getUsername())
                .replace("-d", user.getPlayer().getData().getUsername())
                .replace("-e", this.merge(params));
    }

    @Override
    public String getPermission() {
        return "alert_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.name" + " " + "command.parameter.message", "(usuário) (mensagem)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.alert.description");
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
