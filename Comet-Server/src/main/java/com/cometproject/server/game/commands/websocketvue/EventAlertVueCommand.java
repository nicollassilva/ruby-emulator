package com.cometproject.server.game.commands.websocketvue;

import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class EventAlertVueCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        final JSONObject output = new JSONObject();

        final Room room = client.getPlayer().getEntity().getRoom();
        final int roomId = room.getId();

        output.put("event_name", room.getData().getName());
        output.put("username", client.getPlayer().getData().getUsername());
        output.put("room_id", roomId);

        try {
            final Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.OpenEventAlertMessage);
            final OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();
            final IMessageComposer messageComposer = new RoomForwardMessageComposer(roomId);

            for (final Map.Entry<Integer, ISession> map : SessionManagerAccessor.getInstance().getSessionManager().getSessions().entrySet()) {
                final Session session = (Session) map.getValue();
                if (session == null)
                    continue;

                if (session.getPlayer().getId() == client.getPlayer().getId() || session.getPlayer().getSettings().ignoreEvents())
                    continue;

                if (session.getPlayer().getNitro()) {
                    if (session.getPlayer().getEntity() != null && session.getPlayer().getEntity().getRoom().getData().getId() == roomId)
                        continue;

                    session.send(messageComposer);
                    continue;
                }

                message.client = session.getPlayer().getData().getWebsocketSession();
                message.data = output;
                message.compose();
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | IOException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPermission() {
        return "event_alert_websocket_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.event_alert_websocket.description", "");
    }
}
