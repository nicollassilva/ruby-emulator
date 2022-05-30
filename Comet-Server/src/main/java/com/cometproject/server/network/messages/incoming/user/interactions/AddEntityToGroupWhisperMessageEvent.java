package com.cometproject.server.network.messages.incoming.user.interactions;

import com.cometproject.server.config.Locale;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class AddEntityToGroupWhisperMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {

        String playerName = msg.readString();

        long currentTimeMs = System.currentTimeMillis();

        long timeSinceLastUpdate = currentTimeMs - client.getPlayer().getLastPhotoTaken();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null || timeSinceLastUpdate < (long)(client.getPlayer().getData().getRank() > 6 ? 0 : 5000)) {
            client.send(new NotificationMessageComposer("time_error", Locale.getOrDefault("action.time.error.message", "Você deve esperar 5 segundos a cada ação.")));
            return;
        }

        boolean groupWhisperResult = client.getPlayer().handleGroupWhisper(playerName);

        String actionType = groupWhisperResult ? "adicionou" : "retirou";

        String pronounType = groupWhisperResult ? "ao" : "do";

        client.getPlayer().sendBubble("looks/figure/" + playerName, "Você " + actionType + " " + playerName + " " + pronounType + " chat de grupo.");

        client.getPlayer().setLastPhotoTaken(System.currentTimeMillis());
    }
}
