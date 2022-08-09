package com.cometproject.server.game.commands.staff;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;


public class KickCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            sendNotif(Locale.getOrDefault("command.kick.none", "Quem você quer expulsar?"), client);
            return;
        }

        final String username = params[0];
        final Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            sendNotif(Locale.getOrDefault("command.user.offline", "Esse usuário está offline!"), client);
            return;
        }

        if (username.equals(client.getPlayer().getData().getUsername()))
            return;

        if (user.getPlayer().getEntity() == null) {
            sendNotif(Locale.getOrDefault("command.user.notinroom", "Esse usuário não está em nenhum quarto."), client);
            return;
        }

        if (!user.getPlayer().getPermissions().getRank().roomKickable()) {
            sendNotif(Locale.getOrDefault("command.kick.unkickable", "Você não pode expulsar esse usuário!"), client);
            return;
        }

        if(client.getPlayer().getEntity().getRoom().getData().getOwnerId() == user.getPlayer().getId()) {
            sendNotif(Locale.getOrDefault("command.kick.owner", "Você não pode expulsar o dono do quarto."), client);
            return;
        }

        user.getPlayer().getEntity().kick();
        isExecuted(client);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "-c expulsou o usuário -d"
                .replace("-c", client.getPlayer().getData().getUsername())
                .replace("-d", user.getPlayer().getData().getUsername());
    }

    @Override
    public String getPermission() {
        return "kick_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.username", "(usuário)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.kick.description");
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
