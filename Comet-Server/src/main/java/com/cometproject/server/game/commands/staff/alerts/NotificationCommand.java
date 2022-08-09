package com.cometproject.server.game.commands.staff.alerts;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class NotificationCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if(params.length < 1) {
            client.send(new NotificationMessageComposer("generic", "Por favor, digite a mensagem da notificação."));
            return;
        }

        String image = Locale.getOrDefault("notification.image", "generic");
        String message;

        if (params.length > 1) {
            image = params[0];
            message = this.merge(params, 1);
        } else {
            message = this.merge(params);
        }

        globalNotification(image, message, client);

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s enviou uma notificação com a mensagem '%p'"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%p", message);
    }

    protected void globalNotification(String image, String message, Session client) {
        NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer(image, message));
    }

    @Override
    public String getPermission() {
        return "notification_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.notification.parameter", "%message%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.notification.description");
    }

    @Override
    public String getLoggableDescription(){
        return this.logDesc;
    }

    @Override
    public boolean isLoggable(){
        return true;
    }
}
