package com.cometproject.server.game.commands.staff;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.boot.webhooks.PunishmentWebhook;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;


public class DisconnectCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("command.disconnect.none", "Quem você quer desconectar?"), client);
            return;
        }

        final String username = params[0];

        final Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) {
            sendNotif(Locale.get("command.user.offline"), client);
            return;
        }

        if(session.getPlayer().getData().getRank() > client.getPlayer().getData().getRank()) {
            sendNotif(Locale.getOrDefault("command.disconnect.rank", "Você não pode desconectar uma pessoa de cargo superior ao seu"), client);
            return;
        }

        if (session == client) {
            sendNotif(Locale.get("command.disconnect.himself"), client);
            return;
        }

        if (!session.getPlayer().getPermissions().getRank().disconnectable()) {
            sendNotif(Locale.get("command.disconnect.undisconnectable"), client);
            return;
        }

        if(CometExternalSettings.enableStaffMessengerLogs) {
            this.logDesc = "-c desconectou -d"
                    .replace("-c", client.getPlayer().getData().getUsername())
                    .replace("-d", session.getPlayer().getData().getUsername());
        }

        session.disconnect();
        isExecuted(client);

        PunishmentWebhook.sendDisconnect(client.getPlayer().getData().getUsername(), username);
    }

    @Override
    public String getPermission() {
        return "disconnect_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.disconnect.description");
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
