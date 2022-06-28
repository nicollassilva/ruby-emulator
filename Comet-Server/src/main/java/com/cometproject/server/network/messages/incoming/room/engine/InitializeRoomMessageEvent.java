package com.cometproject.server.network.messages.incoming.room.engine;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.pin.EmailVerificationWindowMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class InitializeRoomMessageEvent implements Event {
    public static final LoadHeightmapMessageEvent heightmapMessageEvent = new LoadHeightmapMessageEvent();

    public void handle(Session client, MessageEvent msg) {
        final int id = msg.readInt();
        final String password = msg.readString();

        if (client.getPlayer() == null) {
            return;
        }

        if (System.currentTimeMillis() - client.getPlayer().getLastRoomRequest() < 500) {
            return;
        }

        if(client.getPlayer().getPermissions().getRank().modTool() && !client.getPlayer().getSettings().isPinSuccess()) {
            client.getPlayer().sendBubble("pincode", Locale.getOrDefault("pin.code.required", "Debes verificar tu PIN antes de realizar cualquier acción."));
            client.send(new EmailVerificationWindowMessageComposer(1,1));
            client.send(new HotelViewMessageComposer());
            return;
        }

        if (client.getPlayer().getEntity() != null && !client.getPlayer().isSpectating(id) && !client.getPlayer().hasQueued(id)) {
            if (!client.getPlayer().getEntity().isFinalized()) {
                client.getPlayer().setEntity(null);
            }
        }

        client.getPlayer().setLastRoomRequest(System.currentTimeMillis());
        RoomManager.getInstance().initializeRoom(client, id, password);
    }
}
