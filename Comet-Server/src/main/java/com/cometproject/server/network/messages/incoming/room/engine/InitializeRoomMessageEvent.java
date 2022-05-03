package com.cometproject.server.network.messages.incoming.room.engine;

import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;


public class InitializeRoomMessageEvent implements Event {
    public static final LoadHeightmapMessageEvent heightmapMessageEvent = new LoadHeightmapMessageEvent();

    public void handle(Session client, MessageEvent msg) {
        int id = msg.readInt();
        final String password = msg.readString();

        if (client.getPlayer() == null) {
            return;
        }

        if (System.currentTimeMillis() - client.getPlayer().getLastRoomRequest() < 500) {
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
