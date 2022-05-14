package com.cometproject.server.game.commands.staff.alerts;

import com.cometproject.api.config.CometExternalSettings;
import com.cometproject.api.config.CometSettings;
import com.cometproject.api.game.GameContext;
import com.cometproject.api.game.rooms.settings.RoomAccessType;
import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.misc.ChatEmotion;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class EventAlertCommand extends ChatCommand {

    private String logDesc = "";

    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            return;
        }

        final int roomId = client.getPlayer().getEntity().getRoom().getId();

        final String imageEvent = Locale.getOrDefault("image.event", "event");

        final IMessageComposer msg = new AdvancedAlertMessageComposer(
                    Locale.get("command.eventalert.alerttitle"),
                    Locale.get("command.eventalert.message")
                            .replace("%message%", this.merge(params))
                            .replace("%username%", client.getPlayer().getData().getUsername()) + "<br><br><b> " + client.getPlayer().getData().getUsername() + "</b>",
                    Locale.get("command.eventalert.buttontitle"), "event:navigator/goto/" + roomId, imageEvent);

        //if (CometSettings.hotelName.equals("Ruby")) {
            for (final ISession session : NetworkManager.getInstance().getSessions().getSessions().values()) {
                if (session.getPlayer() != null && !session.getPlayer().getSettings().ignoreEvents()) {
                    session.send(msg);
                }
            }
        //}

        final Room room = client.getPlayer().getEntity().getRoom();

        room.getData().setAccess(RoomAccessType.OPEN);
        GameContext.getCurrent().getRoomService().saveRoomData(room.getData());

        if(!CometExternalSettings.enableStaffMessengerLogs) return;

        this.logDesc = "Alerta de evento criado por %s no quarto '%b'"
                .replace("%s", client.getPlayer().getData().getUsername())
                .replace("%b", client.getPlayer().getEntity().getRoom().getData().getName());
    }

    @Override
    public String getPermission() {
        return "eventalert_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.eventalert.description");
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
