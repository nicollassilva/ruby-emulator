package com.cometproject.server.game.commands.staff.alerts;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;


public class MassMotdCommand extends ChatCommand {
    private String logDesc;

    @Override
    public void execute(Session client, String[] message) {
        final MotdNotificationMessageComposer msg = new MotdNotificationMessageComposer(this.merge(message) + "\n\n- " + client.getPlayer().getData().getUsername());

        NetworkManager.getInstance().getSessions().broadcast(msg);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "<b>-s</b> enviou um alerta <i>MOTD</i> para o hotel. [<b>-c</b>]"
                .replace("-s", client.getPlayer().getData().getUsername())
                .replace("-c", this.merge(message));
    }

    @Override
    public String getPermission() {
        return "massmotd_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.message", "(mensagem)");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.massmotd.description");
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
