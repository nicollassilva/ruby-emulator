package com.cometproject.server.network.messages.incoming.user.details;

import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.handshake.HomeRoomMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomDataMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.settings.RoomInfoUpdatedMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;
import com.cometproject.server.storage.queries.player.PlayerDao;


public class ChangeHomeRoomMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int roomId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        final Room room = RoomManager.getInstance().get(roomId);

        if(client.getPlayer().getSettings().getHomeRoom() == roomId) {
            return;
        }

        if (client.getPlayer().antiSpam("ChangeHomeRoomMessageEvent", 0.5)) {
            return;
        }

        client.getPlayer().getSettings().setHomeRoom(roomId);

        client.send(new HomeRoomMessageComposer(roomId, roomId));
        client.send(new RoomInfoUpdatedMessageComposer(roomId));
        client.send(new RoomDataMessageComposer(room.getData(), false, false, true, false));

        PlayerDao.updateHomeRoom(roomId, client.getPlayer().getId());
    }
}
