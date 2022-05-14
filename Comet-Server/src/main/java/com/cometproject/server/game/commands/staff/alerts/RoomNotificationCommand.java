package com.cometproject.server.game.commands.staff.alerts;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class RoomNotificationCommand extends NotificationCommand {

    private String logDesc = "";

    @Override
    protected void globalNotification(String image, String message, Session client) {
        for (final PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
            playerEntity.getPlayer().getSession().send(new NotificationMessageComposer(image, message));
        }

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "%s enviou uma notificação no quarto '%b' com a mensagem %p"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%b", client.getPlayer().getEntity().getRoom().getData().getName())
                .replace("%p", message);
    }

    @Override
    public String getPermission() {
        return "roomnotification_command";
    }

    @Override
    public String getParameter() {
        return Locale.getOrDefault("command.parameter.message", "%message%");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roomnotification.description");
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
