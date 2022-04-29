package com.cometproject.server.game.rooms.objects.items.types.floor.wired.actions.custom;

import com.cometproject.api.game.rooms.objects.data.RoomItemData;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.cometproject.server.game.rooms.objects.items.types.floor.wired.events.WiredItemEvent;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.battleball.outgoing.Outgoing;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessage;
import com.cometproject.server.network.battleball.outgoing.OutgoingMessageManager;
import com.cometproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.cometproject.server.network.sessions.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class WiredCustomShouVideoYoutube extends WiredActionItem {
    public WiredCustomShouVideoYoutube(RoomItemData itemData, Room room) {
        super(itemData, room);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public void onEventComplete(WiredItemEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        final PlayerEntity playerEntity = ((PlayerEntity) event.entity);

        if (playerEntity.getPlayer() == null || playerEntity.getPlayer().getSession() == null) {
            return;
        }

        if (this.getWiredData() == null || this.getWiredData().getText() == null) {
            return;
        }

        final String url = this.getWiredData().getText();

        if(url.startsWith("https://youtube.com/watch?v=") ||
                url.startsWith("youtube.com/watch?v=") ||
                url.startsWith("https://www.youtube.com/watch?v=") ||
                url.startsWith("www.youtube.com/watch?v=")) {
            try {
                this.sendRoomVideoWindow(playerEntity.getPlayer().getSession(), url);
            } catch (NoSuchMethodException | IllegalAccessException | IOException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendRoomVideoWindow(Session client, String url) throws NoSuchMethodException, IllegalAccessException, IOException, InstantiationException, InvocationTargetException {
        final Class<? extends OutgoingMessage> classMessage = OutgoingMessageManager.getInstance().getMessages().get(Outgoing.OpenYoutubeWindowMessage);
        final OutgoingMessage message = classMessage.getDeclaredConstructor().newInstance();

        message.data = new JSONObject();
        message.data.put("url", url.replace("watch?v=", "embed/") + "?autoplay=1&controls=0");

        for (final PlayerEntity entity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
            final org.eclipse.jetty.websocket.api.Session wsSession = entity.getPlayer().getData().getWebsocketSession();

            if(wsSession == null) continue;

            message.client = wsSession;
            message.compose();
        }
    }
}
