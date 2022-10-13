package com.cometproject.server.game.commands.user.ws;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.sessions.Session;
import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class RoomVideoCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            sendNotif(Locale.getOrDefault("comand.roomvideo.missing_url", "Por favor, insira a URL do vídeo."), client);
            return;
        }

        if(client.getPlayer().getEntity() == null)
            return;

        final boolean isOwner = client.getPlayer().getId() == client.getPlayer().getEntity().getRoom().getData().getOwnerId();
        final boolean hasRights = client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId());

        if(!isOwner && !hasRights && !client.getPlayer().getPermissions().getRank().roomFullControl())
            return;

        String url = params[0];

        // Fix
        if(url.contains("&")) {
            url = url.split("&")[0];
        }

        if(url.startsWith("https://youtube.com/watch?v=") ||
                url.startsWith("youtube.com/watch?v=") ||
                url.startsWith("https://www.youtube.com/watch?v=") ||
                url.startsWith("www.youtube.com/watch?v=")
        ) {
            try {
                this.sendRoomVideoWindow(client, url);
            } catch (NoSuchMethodException | IllegalAccessException | IOException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            sendNotif(Locale.getOrDefault("command.roomvideo.youtube_urls_only", "Por favor, use apenas URL provindas do Youtube."), client);
        }
    }


    public void sendRoomVideoWindow(Session client, String url) throws NoSuchMethodException, IllegalAccessException, IOException, InstantiationException, InvocationTargetException {
        final Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.OpenYoutubeWindowMessage);
        final OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();

        message.data = new JSONObject();
        message.data.put("url", url.replace("watch?v=", "embed/") + "?autoplay=1&controls=1");

        for (final PlayerEntity entity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
            final WebSocket wsSession = entity.getPlayer().getData().getWebsocketSession();

            if(wsSession == null) continue;

            message.client = wsSession;
            message.compose();
        }
    }

    @Override
    public String getPermission() {
        return "roomvideo_command";
    }

    @Override
    public String getParameter() {
        return "(url do vídeo)";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roomvideo.description");
    }
}
