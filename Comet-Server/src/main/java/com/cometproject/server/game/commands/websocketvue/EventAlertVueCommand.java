package com.cometproject.server.game.commands.websocketvue;

import com.cometproject.api.config.CometSettings;
import com.cometproject.api.networking.sessions.ISession;
import com.cometproject.api.networking.sessions.SessionManagerAccessor;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.discord.Webhook;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.sessions.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class EventAlertVueCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        final JSONObject output = new JSONObject();

        output.put("event_name", client.getPlayer().getEntity().getRoom().getData().getName());
        output.put("username", client.getPlayer().getData().getUsername());
        output.put("room_id", client.getPlayer().getEntity().getRoom().getId());

        try {
            final Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.OpenEventAlertMessage);
            final OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();

            for (final Map.Entry<Integer, ISession> map : SessionManagerAccessor.getInstance().getSessionManager().getSessions().entrySet()) {
                final Session session = (Session) map.getValue();

                if(session.getPlayer().getId() == client.getPlayer().getId() || session.getPlayer().getSettings().ignoreEvents())
                    return;

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
        return Locale.getOrDefault("command.event_alert_websocket.parameters", "%evento%");
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.event_alert_websocket.description", "");
    }
}
